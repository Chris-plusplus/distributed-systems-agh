import ray
from NameNode import NameNode
from StorageNode import StorageNode
import Config
from Chunk import *

def update(name: str, storageResult: tuple[list[list[tuple[int, ray.ObjectRef]]], list[list[tuple[int, ray.ObjectRef]]], list[list[tuple[int, ray.ObjectRef]]]], data: str):
    chunks = Chunks.make(data)
    toUpdate, toRemove, toAdd = storageResult
    if toRemove != None:
        for chunkIdx, nodeList in enumerate(toRemove):
            for _, node in nodeList:
                ray.get(node.remove.remote(name, chunkIdx + len(toUpdate)))
    for chunkIdx, nodeList in enumerate(toUpdate):
        for _, node in nodeList:
            ray.get(node.update.remote(name, chunkIdx, chunks[chunkIdx], len(toUpdate), len(toUpdate) if toAdd == None else len(toUpdate) + len(toAdd)))
    if toAdd != None:
        for chunkIdx, nodeList in enumerate(toAdd):
            for _, node in nodeList:
                ray.get(node.store.remote(name, chunkIdx + len(toUpdate), chunks[chunkIdx + len(toUpdate)], len(toAdd)))