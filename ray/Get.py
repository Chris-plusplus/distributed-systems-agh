import ray
import Config

def get(name: str, storageResult: list[list[tuple[int, ray.ObjectRef]]]):
    nodes = [subStorage[0] for subStorage in storageResult]
    result: str = ""
    for i, pair in enumerate(nodes):
        result += ray.get(pair[1].get.remote(name, i))
    return result