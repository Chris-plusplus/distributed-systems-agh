import Config
import ray

@ray.remote
class StorageNode:
    def __init__(self, nameNode, i):
        self.storage: dict[str, dict[int, str]] = {}
        self.nameNode = nameNode
        self.i = i

    def ping(self):
        return True
    
    def __repr__(self):
        return f'{self.i}'
    
    def assure(self, name):
        found = self.storage.get(name)
        if found != None:
            return found
        self.storage[name] = {}
        return self.storage[name]

    def store(self, name, chunkIdx, data, chunkCount):
        self.assure(name)[chunkIdx] = data
        self.nameNode.storedData.remote(self.i, name, chunkIdx, chunkCount)
        print(self.state())

    def restore(self, name, chunkIdx, data):
        self.assure(name)[chunkIdx] = data
        print(self.state())

    def update(self, name, chunkIdx, data, chunkCount, chunkCountFull):
        if name not in self.storage:
            return None
        self.storage[name][chunkIdx] = data
        self.nameNode.updatedData.remote(self.i, name, chunkIdx, chunkCount, chunkCountFull)
        print(self.state())
    
    def postUpdate(self, name, chunkCount):
        self.nameNode.postUpdate.remote(name, chunkCount)
        print(self.state())

    def remove(self, name, chunkIdx):
        if name not in self.storage:
            return None
        self.storage[name].pop(chunkIdx)
        if len(self.storage[name]) == 0:
            self.storage.pop(name)
        self.nameNode.removedData.remote(self.i, name, chunkIdx)
        print(self.state())

    def get(self, name, i):
        storage = self.storage.get(name)
        if storage == None:
            return None
        return storage.get(i)
    
    def state(self):
        return {key: [k for k in val.keys()] for key, val in self.storage.items()}
    
    def stateOf(self, name):
        storage = self.storage.get(name)
        if storage == None:
            return None
        return [k for k in storage.keys()]