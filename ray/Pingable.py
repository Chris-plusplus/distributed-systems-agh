import ray

def pingable(_class):
    def ping(self):
        return True
    
    setattr(_class, 'ping', ping)
    return _class