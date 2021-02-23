from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait

import json
from scrape_utils import parse_macro


def scrape_condition(cells):
    cond_id = cells[0].text
    cells.pop(0)
    value_types = scrape_value_types(cells)

    condition = {
        'id': cond_id,
        'value_types': value_types,
        'macro': []
    }
    parse_macro(condition)

    return condition


def scrape_value_types(cells):
    value_type = cells[0].text
    value_types = parse_value_types(value_type)
    description = cells[1].text
    scope = cells[2].text
    example = cells[3].text
    value_types = map(lambda vt: value_type_to_type_config(vt, description, scope, example), value_types)
    return list(value_types)


def parse_value_types(value_type):
    value_types = value_type.split(",")
    value_types = map(lambda vt: vt.strip(), value_types)
    value_types = map(cleanup_value_type, value_types)
    return list(value_types)


def cleanup_value_type(value_type):
    value_type = value_type.lower().split(" ")
    stripped = map(lambda vt: vt.strip(), value_type)
    capitalized = map(lambda vt: vt.capitalize(), stripped)
    value_type = ''.join(capitalized)
    return value_type


def value_type_to_type_config(value_type, description, scope, example):
    return {
        'type': value_type,
        'description': description,
        'scope': scope,
        'example': example
    }


with webdriver.Chrome() as driver:
    wait = WebDriverWait(driver, 10)
    driver.get('https://eu4.paradoxwikis.com/Conditions')
    rows = driver.find_elements(By.XPATH, '//table[./thead/tr/th[5][contains(text(),"Example")]]/tbody/tr')
    conditions = {}
    prev_condition = None
    for row in rows:
        cells = row.find_elements(By.TAG_NAME, 'td')
        if len(cells) == 5:
            c = scrape_condition(cells)
            conditions[c['id']] = c
            prev_condition = c
        elif len(cells) == 4:
            vts = scrape_value_types(cells)
            prev_condition['value_types'].extend(vts)

    with open("eu4_conditions.json", "w") as data_file:
        json.dump(conditions, data_file, indent=4, sort_keys=False)

    print(f"Parsed {len(conditions)} conditions")
