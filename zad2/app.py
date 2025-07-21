from fastapi import FastAPI, status
from enum import Enum
from copy import deepcopy
from fastapi.responses import JSONResponse, HTMLResponse
from fastapi.encoders import jsonable_encoder
from pydantic import BaseModel
from asyncio import *
import httpx
from datetime import datetime

app = FastAPI()

def isHashable(obj):
    try:
        hash(obj)
        return True
    except TypeError:
        return False

MIN_COUNT = 1
MAX_COUNT = 59
DEFAULT_COUNT = 25

MOST = {"starred": "Starred", "forked": "Forked (Github only)"}
SCORINGS = {"top": "Top Language", "proportional": "Proportional"}

@app.get("/")
async def index_html():
    content = None
    with open("index.html") as file:
        content = file.read()

    content = content.replace("$minCount", str(MIN_COUNT))
    content = content.replace("$maxCount", str(MAX_COUNT))
    content = content.replace("$defaultCount", str(DEFAULT_COUNT))

    mostOptions = "".join([f'<option value="{k}">{v}</option>' for k, v in MOST.items()])
    content = content.replace("$most", mostOptions)

    scoringOptions = "".join([f'<option value="{k}">{v}</option>' for k, v in SCORINGS.items()])
    content = content.replace("$scoring", scoringOptions)

    return HTMLResponse(content = content, status_code=status.HTTP_200_OK)

def normalized(langs: dict):
    sum = 0
    for val in langs.values():
        if type(val) == str:
            print(val)
            raise TypeError()
        sum += val

    return {k: v / sum for k, v in langs.items()}

async def githubScores(mostX: str, count: int, scoring: str):
    result = None
    method = "stars" if mostX == "starred" else "forks"
    with httpx.Client() as client:
        result = client.get(f"https://api.github.com/search/repositories?q={method}:%3E0&sort={method}&order=desc&per_page={count}")
    if result.status_code != status.HTTP_200_OK:
        #print(result.status_code)
        tempJson = result.json()
        #print(tempJson)
        try:
            tempJson['x-ratelimit-reset'] = datetime.fromtimestamp(result.headers.get('x-ratelimit-reset')).strftime('%Y-%m-%d %H:%M:%S')
        except:
            pass
        return {}, {"errors": {"github.com": [tempJson]}}
    json = result.json()

    mostX = {"starred": "stargazers_count", "forked": "forks_count"}[mostX]

    repoInfo: list[tuple[Task[httpx.Response], int, str]] = []
    scores = {}
    others = {}

    others['errors'] = {}
    others['errors']["github.com"] = []

    others["limits"] = {}
    others["limits"]["github.com"] = {}
    others["limits"]["github.com"]['availableRequests'] = MAX_COUNT

    async with httpx.AsyncClient() as client:
        first = True
        loop = True
        for repo in json['items']:
            request = f"{repo['url']}/languages"
            repoInfo.append((create_task(client.get(url=request)), repo[mostX], request))
            if first:
                first = False
                getResult, repoScore, req = repoInfo[-1]
                res = await getResult
                if res.status_code - 200 >= 100:
                    #raise ValueError(jsonable_encoder(res.json()))
                    #print(f'{req}\n{res.headers}')
                    try:
                        others['limits']["github.com"]['availableRequests'] = min(others['limits']["github.com"]['availableRequests'], max(0, int(res.headers.get('x-ratelimit-remaining'))))
                    except:
                        pass
                    try:
                        others['limits']["github.com"]['ratelimit-reset'] = res.headers.get('x-ratelimit-reset')
                    except:
                        pass

                    others['errors']["github.com"].append(res.json())
                    if others['limits']["github.com"]['availableRequests'] == 0 or res.status_code == status.HTTP_429_TOO_MANY_REQUESTS:
                        loop = False
                        others['limits']["github.com"]['availableRequests'] = 0
                        break
                    continue
                
                try:
                    others['limits']["github.com"]['availableRequests'] = min(others['limits']["github.com"]['availableRequests'], max(0, int(res.headers.get('x-ratelimit-remaining'))))
                except:
                    pass
                try:
                    others["limits"]["github.com"]["ratelimit-reset"] = res.headers.get('x-ratelimit-reset')
                except:
                    pass
                result = normalized(res.json())
                if scoring == "top":
                    for lang, percentage in sorted(result.items(), key=lambda p: p[1], reverse=True):
                        if lang in scores:
                            scores[lang] += repoScore
                        else:
                            scores[lang] = repoScore
                        break
                elif scoring == "proportional":
                    for lang, percentage in result.items():
                        if lang in scores:
                            scores[lang] += percentage * repoScore
                        else:
                            scores[lang] = percentage * repoScore

        if loop:
            for getResult, repoScore, req in repoInfo[1:]:
                res = await getResult
                if res.status_code - 200 >= 100:
                    #raise ValueError(jsonable_encoder(res.json()))
                    #print(req)
                    #print(res.headers)
                    try:
                        others['limits']["github.com"]['availableRequests'] = min(others['limits']["github.com"]['availableRequests'], max(0, int(res.headers.get('x-ratelimit-remaining'))))
                    except:
                        pass
                    try:
                        others["limits"]["github.com"]["ratelimit-reset"] = res.headers.get('x-ratelimit-reset')
                    except:
                        pass

                    others['errors']["github.com"].append(res.json())
                    if others['limits']["github.com"]['availableRequests'] == 0 or res.status_code == status.HTTP_429_TOO_MANY_REQUESTS:
                        break
                    continue

                try:
                    others['limits']["github.com"]['availableRequests'] = min(others['limits']["github.com"]['availableRequests'], max(0, int(res.headers.get('x-ratelimit-remaining'))))
                except:
                    pass
                try:
                    others["limits"]["github.com"]["ratelimit-reset"] = res.headers.get('x-ratelimit-reset')
                except:
                    pass
                result = normalized(res.json())
                if scoring == "top":
                    for lang, percentage in sorted(result.items(), key=lambda p: p[1], reverse=True):
                        if lang in scores:
                            scores[lang] += repoScore
                        else:
                            scores[lang] = repoScore
                        break
                elif scoring == "proportional":
                    for lang, percentage in result.items():
                        if lang in scores:
                            scores[lang] += percentage * repoScore
                        else:
                            scores[lang] = percentage * repoScore
    
    if len(others["errors"]["github.com"]) == 0:
        others.pop("errors")
    others["limits"]["github.com"]["ratelimit-reset"] = datetime.fromtimestamp(int(others["limits"]["github.com"]["ratelimit-reset"])).strftime('%Y-%m-%d %H:%M:%S')
    return scores, others


async def gitlabScores(mostX: str, count: int, scoring: str, host: str):
    if mostX != "starred":
        return {}, {"errors": {host: ["mostX != starred"]}}
    
    result = None
    with httpx.Client() as client:
        result = client.get(f"https://{host}/api/v4/projects?order_by=star_count&sort=desc&per_page={count}")
    if result.status_code - 200 >= 100:
        return {}, {"errors": {host: [result.status_code]}}

    json = result.json()

    mostX = "star_count"

    repoInfo: list[tuple[Task[httpx.Response], int, str]] = []
    scores = {}
    others = {}
    
    others["errors"] = {}
    others["errors"][host] = []

    async with httpx.AsyncClient() as client:
        first = True
        loop = True
        for repo in json:
            request = f"https://{host}/api/v4/projects/{repo["id"]}/languages"
            repoInfo.append((create_task(client.get(url=request)), repo[mostX], request))
            if first:
                first = False
                getResult, repoScore, req = repoInfo[-1]
                res = await getResult
                if res.status_code - 200 >= 100:
                    #print(f'{req}\n{res.headers}')
                    others["errors"][host].append(res.json())
                    if res.status_code == status.HTTP_429_TOO_MANY_REQUESTS:
                        loop = False
                        break
                    continue
                result = normalized(res.json())
                if scoring == "top":
                    for lang, percentage in sorted(result.items(), key=lambda p: p[1], reverse=True):
                        scores[lang] = repoScore
                        break
                elif scoring == "proportional":
                    for lang, percentage in result.items():
                        scores[lang] = percentage * repoScore

        if loop:
            for getResult, repoScore, req in repoInfo[1:]:
                res = await getResult
                if res.status_code - 200 >= 100:
                    #print(f'{req}\n{res.headers}')
                    others["errors"][host].append(res.json())
                    if res.status_code == status.HTTP_429_TOO_MANY_REQUESTS:
                        break
                    continue
                result = normalized(res.json())
                if scoring == "top":
                    for lang, percentage in sorted(result.items(), key=lambda p: p[1], reverse=True):
                        scores[lang] = repoScore
                        break
                elif scoring == "proportional":
                    for lang, percentage in result.items():
                        scores[lang] = percentage * repoScore

    if len(others["errors"][host]) == 0:
        others = {}

    return scores, others


VALUE_ERRORS = {"mostX", "count", "scoring"}

async def getProjectsJSON(mostX: str, count: int, scoring: str, customGitlabHosts: str):
    gitlabHosts = ["gitlab.com"] + customGitlabHosts.splitlines()

    if mostX not in MOST:
        raise ValueError("mostX")
    if MIN_COUNT > count or count > MAX_COUNT:
        raise ValueError("count")
    elif scoring not in SCORINGS:
        raise ValueError("scoring")
    
    tasks = [create_task(githubScores(mostX, count, scoring))]
    for host in gitlabHosts:
        tasks.append(create_task(gitlabScores(mostX, count, scoring, host)))
    
    finalResult = {}
    for task in tasks:
        try:
            scores, others = await task
            #print(scores)
            finalResult |= others
            for lang, score in scores.items():
                if lang not in finalResult:
                    finalResult[lang] = score
                else:
                    finalResult[lang] += score
        except Exception as e:
            print(e)

    return finalResult
    
@app.get("/projects")
async def getProjects(mostX: str, count: int, scoring: str, customGitlabHosts: str = ""):
    try:
        json = await getProjectsJSON(mostX, count, scoring, customGitlabHosts)

        html = f"<h1>Most {mostX} languages</h1><br>"

        html += "Hosts:<br>"
        hosts = ["github.com", "gitlab.com"] + customGitlabHosts.splitlines()
        for i, h in enumerate(hosts):
            html += f"\t{i+1}. {h}<br>"
        
        if "errors" in json:
            html += "<br>Errors:<br>"
            for h, errs in json["errors"].items():
                html += f"From {h}:<br>"
                assert(type(errs) == list)
                for i, err in enumerate(errs):
                    html += f"{i+1}. {err["message"]}<br>"
            json.pop("errors")

        if "limits" in json:
            html += f"<br>Limits:<br>Available requests (github.com): {json["limits"]["github.com"]["availableRequests"]}<br>Reset time: {json["limits"]["github.com"]["ratelimit-reset"]}<br>"

            json.pop("limits")

        jsonNormalized = normalized(json)        
        langs = sorted([(lang, float(score), float(jsonNormalized[lang])) for lang, score in json.items()], key=lambda p: p[1], reverse=True)

        html += "<br><br>"

        for i, (lang, score, percent) in enumerate(langs):
            html += f"<h2>{i+1}. {lang}, score: {score} -> {percent * 100}%</h2>"

        return HTMLResponse(content=html)

    except ValueError as e:
        return HTMLResponse(content = f'<h1>Parameter \'{e.args[0]}\' has incorrect value \'{count if e.args[0] == "count" else scoring}\'</h1>', status_code=status.HTTP_400_BAD_REQUEST)
    
@app.get("/api/projects")
async def getProjectsAPI(mostX: str, count: int, scoring: str, customGitlabHosts: str = ""):
    try:
        json = await getProjectsJSON(mostX, count, scoring, customGitlabHosts)
        return JSONResponse(content=json, status_code=status.HTTP_200_OK)
    except ValueError as e:
        if isHashable(e.args[0]) and e.args[0] in VALUE_ERRORS:
            return JSONResponse(content = jsonable_encoder({"message":f"Parameter \'{e.args[0]}\' has incorrect value \'{count if e.args[0] == "count" else scoring}\'"}), status_code=status.HTTP_400_BAD_REQUEST)
        else:
            return JSONResponse(content = e.args[0], status_code=status.HTTP_400_BAD_REQUEST)