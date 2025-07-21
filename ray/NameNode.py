from ray.runtime_context import RuntimeContext
import Config
import ray
from StorageNode import *
import random
from threading import Lock
from Chunk import *

@ray.remote
class NameNode:
    def __init__(self):
        self.nodeCounter = 0
        # where to find name, list where i-th sublist contains StorageNodes for i-th chunk 
        self.locations: dict[str, list[list[int]]] = {}
        self.chunkCounts: dict[str, int] = {}
        self.updateCounter: dict[str, int] = {}
        # whether name has all chunks
        self.full: dict[str, bool] = {}
        self.storageNodes = [self.newStorageNode(i) for i in range(Config.STORAGE_NODE_COUNT)]
        self.alive = [True for _ in range(Config.STORAGE_NODE_COUNT)]
        # what chunks are stored in node
        self.stored = [dict[str, set[int]]() for _ in range(Config.STORAGE_NODE_COUNT)]
        self.lock = Lock()

    def newStorageNode(self, i):
        result = StorageNode.options(name = f'StorageNode{self.nodeCounter}').remote(ray.get_runtime_context().current_actor, i)
        print(f'new node: StorageNode{self.nodeCounter}, @ {i}')
        self.nodeCounter += 1
        return result

    def getLocations(self):
        return self.locations
    def getStored(self):
        return self.stored

    def __getNoFix(self, name: str, i: int):
        for location in self.locations[name][i]:
            if self.alive[location]:
                return ray.get(self.storageNodes[location].get.remote(name, i))
        return None

    def __fixNode(self, i: int):
        newNode = self.newStorageNode(i)
        print(f'recovering node {i}:')
        for name, chunkIdxs in self.stored[i].items():
            for chunkIdx in chunkIdxs:
                found = self.__getNoFix(name, chunkIdx)
                if found == None:
                    print(f'lost chunk {chunkIdx} for "{name}"')
                    self.full[name] = False 
                    self.locations[name][chunkIdx] = []
                ray.get(newNode.restore.remote(name, chunkIdx, found))
                self.storageNodes[i] = newNode
                print(f'\trecovered chunk {chunkIdx} for "{name}"')

    def __fixNodes(self):
        dead = list(map(lambda x: x[0], filter(lambda y: not y[1], enumerate(self.alive))))
        if len(dead) != 0:
            print(f'found dead nodes: {dead}')
        for i in dead:
            self.__fixNode(i)

    def assure(self):
        with self.lock:
            for i, node in enumerate(self.storageNodes):
                try:
                    ray.get(node.ping.remote(), timeout = 5)
                except:
                    self.alive[i] = False
            
            self.__fixNodes()

    def storedData(self, nodeIdx: int, name: str, chunkIdx: int, chunkCount: int):
        with self.lock:
            if name not in self.stored[nodeIdx]:
                self.stored[nodeIdx][name] = set()
                self.chunkCounts[name] = chunkCount
                self.updateCounter[name] = -1
            self.stored[nodeIdx][name].add(chunkIdx)
            if name not in self.locations:
                self.locations[name] = [list() for _ in range(chunkCount)]
            
            if len(self.locations[name]) < chunkIdx + 1:
                self.locations[name].append(list())
            self.locations[name][chunkIdx].append(nodeIdx)
            print(f'stored data "{name}" chunk {chunkIdx} in {nodeIdx}')

            # print(self.locations)
            # print(self.stored)
            # print()
    
    def updatedData(self, nodeIdx: int, name: str, chunkIdx: int, chunkCount: int, chunkCountFull: int):
        with self.lock:
            if name not in self.stored[nodeIdx]:
                return None
            if self.updateCounter[name] == -1:
                self.updateCounter[name] = 0
                self.stored[nodeIdx][name] = set()
                self.locations[name] = [list() for _ in range(chunkCount)]
            
            # print(f"before {self.updateCounter[name]} update")
            # print(f'{self.stored}')
            # print(f'{self.locations}')
            self.stored[nodeIdx][name].add(chunkIdx)
            self.locations[name][chunkIdx].append(nodeIdx)
            # print(f"after {self.updateCounter[name]} update")
            # print(f'{self.stored}')
            # print(f'{self.locations}')
            # print()
            self.updateCounter[name] += 1

            if self.updateCounter[name] == chunkCount * Config.CHUNK_COPIES:
                self.updateCounter[name] = -1
                self.chunkCounts[name] = chunkCountFull

    def removedData(self, nodeIdx: int, name: str, chunkIdx: int):
        with self.lock:
            if name not in self.stored[nodeIdx]:
                return None
            self.locations[name][chunkIdx].remove(nodeIdx)

            while len(self.locations[name][-1]) == 0:
                self.locations[name].pop()
                if len(self.locations[name]) == 0:
                    self.locations.pop(name)
                    self.updateCounter.pop(name)
                    self.chunkCounts.pop(name)
                    try:
                        self.full.pop(name)
                    except:
                        pass
                    break

            self.stored[nodeIdx][name].remove(chunkIdx)
            if len(self.stored[nodeIdx][name]) == 0:
                self.stored[nodeIdx].pop(name)

    def storage(self, name: str, chunkCount: int) -> list[list[tuple]]:
        self.assure()
        with self.lock:
            if name in self.locations:
                print(f'storage(): "{name}" in locations')
                return list(map(lambda listForChunk: [(i, self.storageNodes[i]) for i in listForChunk], self.locations[name]))
            else:
                print(f'storage(): "{name}" not in locations')
                result = []
                nodesI = list(enumerate(self.storageNodes))
                for chunk in range(chunkCount):
                    result.append(random.sample(nodesI, Config.CHUNK_COPIES))
                return result
    
    def getStorage(self, name: str):
        self.assure()
        with self.lock:
            if name not in self.locations:
                return None
            return list(map(lambda listForChunk: [(i, self.storageNodes[i]) for i in listForChunk], self.locations[name]))
    
    def updateStorage(self, name: str, chunkCount: int):
        with self.lock:
            newChunkCount = chunkCount
            currChunkCount = self.chunkCounts[name]

            if newChunkCount == currChunkCount:
                return list(map(lambda listForChunk: [(i, self.storageNodes[i]) for i in listForChunk], self.locations[name])), None, None
            elif newChunkCount > currChunkCount:
                result = list(map(lambda listForChunk: [(i, self.storageNodes[i]) for i in listForChunk], self.locations[name]))
                nodesI = list(enumerate(self.storageNodes))
                toAdd = []
                for chunk in range(currChunkCount, newChunkCount):
                    toAdd.append(random.sample(nodesI, Config.CHUNK_COPIES))
                return result, None, toAdd
            elif newChunkCount < currChunkCount:
                result = list(map(lambda listForChunk: [(i, self.storageNodes[i]) for i in listForChunk], self.locations[name][0:newChunkCount]))
                toRemove = list(map(lambda listForChunk: [(i, self.storageNodes[i]) for i in listForChunk], self.locations[name][newChunkCount:]))
                return result, toRemove, None
    
    def removeStorage(self, name: str):
        with self.lock:
            return list(map(lambda listForChunk: [(i, self.storageNodes[i]) for i in listForChunk], self.locations[name]))
        
    def state(self):
        nodeStates = {i: ray.get(node.state.remote()) for i, node in enumerate(self.storageNodes)}
        return f'{self.locations}\n{self.stored}\n{nodeStates}'