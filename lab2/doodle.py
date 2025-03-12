from fastapi import FastAPI, status
from enum import Enum
from copy import deepcopy
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder
from pydantic import BaseModel

app=FastAPI()

# – User can create poll (see what is insider poll)
# – User can cast a vote inside this polls
# – User can add, update and delete all information
# he provides
# – User can see the results of votes


class Vote(BaseModel):
    pollName: str
    option: str

class Poll(BaseModel):
    name: str
    options: list[str]

class PollResults(BaseModel):
    results: dict[str, int]

@app.get("/")
async def root():
    return {"message": "Doodle API to create polls"}

pollDB: dict[str, tuple[Poll, PollResults]] = {}

@app.post("/polls/{pollName}")
async def createPoll(poll: Poll):
    found = pollDB.get(poll.name)
    if found != None:
        
        return JSONResponse(status_code=status.HTTP_409_CONFLICT, content=f"{{\"message\": \"Poll '{found[0].name}' already exists\"}}")
    else:
        pollDB[poll.name] = (poll, {opt: 0 for opt in poll.options})
        return JSONResponse(status_code=status.HTTP_201_CREATED, content="")

@app.get("/polls/{pollName}")
async def viewPoll(pollName: str):
    found = pollDB.get(pollName)
    if found == None:
        return JSONResponse(status_code=status.HTTP_404_NOT_FOUND, content="")
    else:
        return JSONResponse(status_code=status.HTTP_200_OK, content=jsonable_encoder({"options": found[1]}))
    
@app.put("/polls/{pollName}")
async def castVote(vote: Vote):
    found = pollDB.get(vote.pollName)
    if found == None:
        return JSONResponse(status_code=status.HTTP_404_NOT_FOUND, content="")
    else:
        found[1][vote.option] += 1
        return JSONResponse(status_code=status.HTTP_200_OK, content="")