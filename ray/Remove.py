import ray
from NameNode import NameNode
from StorageNode import StorageNode
import Config
from Chunk import *

def remove(name: str, storageResult: list[list[tuple[int, ray.ObjectRef]]]):
    for chunkIdx, nodeList in enumerate(storageResult):
        for _, node in nodeList:
            node.remove.remote(name, chunkIdx)