import random
import json
import math

words = [
    'perfect',
    'dreary',
    'best',
    'yard',
    'inflate',
    'heavy',
    'bird',
    'clocks',
    'rough',
    'sky',
    'imaginary',
    'wary',
    'placid',
    'seem',
    'aggressive',
    'recess',
    'bleed',
    'select',
    'coil',
    'scorch',
    'boats',
    'eye',
    'descriptive',
    'numberless',
    'rake',
    'include',
    'ring',
    'glove',
    'steam',
    'digestion',
    'itchy',
    'lend',
    'second-hand',
    'slow',
    'grumpy',
    'flame',
    'adhesive',
    'remake',
    'unnatural',
    'gainful',
    'xenophobic',
    'fax',
    'lying',
    'end',
    'morning',
    'connect',
    'scene',
    'loving',
    'flash',
    'nail',
    'feather',
    'hilarious',
    'snail',
    'reminiscent',
    'sophisticated',
    'able',
    'rotten',
    'meet',
    'story',
    'prison',
    'vivacious',
    'pocket',
    'reuse',
    'bright',
    'hot',
    'lift',
    'like',
    'likeable',
    'cat',
    'impose',
    'nurse',
    'paint',
    'practise',
    'imprint',
    'excited',
    'funny',
    'kittens',
    'reach',
    'abusive',
    'divergent',
    'available',
    'lead',
    'pluck',
    'thrive',
    'shine',
    'blue',
    'uproot',
    'replace',
    'dull',
    'right',
    'thoughtful',
    'statement',
    'dramatic',
    'truck',
    'explore',
    'instinctive',
    'juvenile',
    'die',
    'waste',
    'bleed',
    'me',
    'i',
    'do',
    'if',
    'unpredictable',
    'be',
    'it',
    'has',
    'am'
]

words_length = len(words)

words_counter = {}

def create_word():
    word_index = min(words_length - 1, math.floor(max(0, random.gauss(50, 20))))
    word = words[word_index]

    word_count = words_counter.setdefault(word, 0)
    words_counter[word] = word_count + 1

    return word

def create_line():
    line_words_number = random.randint(1, 101)
    line_words = []

    for word_index in range(1, line_words_number):
        new_word = create_word()
        line_words.append(new_word)

    return ' '.join(line_words) + '\n'

if __name__ == '__main__':
    for file_index in range(1, 101):
        with open('./words-' + str(file_index) + '.txt', 'w') as file:
            for line_index in range(0, 100001):
                new_line = create_line()
                file.write(new_line)

    with open('./words-frequencies.log', 'w') as dict_file:
        json.dump(words_counter, dict_file, sort_keys = True, indent = 2)

    sorted_words_counter = dict(sorted(words_counter.items(), key = lambda item: item[1]))
    with open('./words-frequencies-sorted.log', 'w') as dict_file:
        json.dump(sorted_words_counter, dict_file, indent = 2)
