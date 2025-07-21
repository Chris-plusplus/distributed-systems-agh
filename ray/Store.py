from StorageNode import StorageNode
import ray
from Chunk import *

def store(name: str, storageResult: list[list[tuple[int, ray.ObjectRef]]], data: str):
    chunks = Chunks.make(data)
    for chunkID, nodeList in enumerate(storageResult):
        for _, node in nodeList:
            node.store.remote(name, chunkID, chunks[chunkID], len(chunks))
    # nameNode.accept.remote(storageResult)