import Config
from math import ceil

class Chunks:
    @staticmethod
    def count(length: int):
        return int(ceil(length / Config.CHUNK_SIZE))
    
    @staticmethod
    def make(data: str) -> list[str]:
        chunkCount = Chunks.count(len(data))
        return [data[i * Config.CHUNK_SIZE:(i + 1) * Config.CHUNK_SIZE] for i in range(chunkCount)]