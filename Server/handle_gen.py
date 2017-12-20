from random import randint

def getWord(WORDS):
    word = WORDS[randint(0, len(WORDS)-1)].capitalize()
    if len(word) >= 6 :
        return getWord(WORDS)
    return word

def getHandle():
    word_file = "./wordlist.txt"
    WORDS = open(word_file).read().splitlines()
    name = ""
    for x in range(0,3):
        name = name + getWord(WORDS)
    return name
