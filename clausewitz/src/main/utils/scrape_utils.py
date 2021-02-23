import re

macro_pattern = '<((?:\\w*\\s*)*)>'
macro_regex = re.compile(macro_pattern)


def parse_macro(obj):
    macro = re.findall(macro_regex, obj['id'])
    if len(macro) > 0:
        macro = list(map(normalize_id, macro))
        obj['macro'] = macro
        obj['id'] = normalize_id(obj['id'])


def normalize_id(m):
    without_spaces = m.split(" ")
    stripped = map(lambda e: e.lower().strip(), without_spaces)
    non_empty = filter(lambda e: e, stripped)
    return "_".join(list(non_empty))
