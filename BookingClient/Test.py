import random

ctr = 0

def increase_ctr():
    global ctr
    ctr = ctr + 1
    return ctr

class RandomNo:
    def __init__(self, range):
        self.range = range

    def getRandom(self):
        random.seed(ctr)
        print('ctr = '+str(ctr))
        no = random.randint(0, self.range)
        print('rand = '+str(no))

r = RandomNo(10)
s = RandomNo(10)
t = RandomNo(10)
u = RandomNo(10)
v = RandomNo(10)
w = RandomNo(10)

r.getRandom()
s.getRandom()
t.getRandom()
u.getRandom()
v.getRandom()
w.getRandom()