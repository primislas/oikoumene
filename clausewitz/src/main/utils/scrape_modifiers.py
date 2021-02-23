from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait

import json
from scrape_utils import parse_macro

with webdriver.Chrome() as driver:
    wait = WebDriverWait(driver, 10)
    driver.get('https://eu4.paradoxwikis.com/Modifier_list')
    rows = driver.find_elements(By.XPATH, '//table[./thead/tr/th[5][contains(text(),"Version")]]/tbody/tr')
    modifiers = {}
    for row in rows:
        cells = row.find_elements(By.TAG_NAME, 'td')
        mod_id = cells[0].text
        example = cells[1].text
        parts = example.split(' = ')
        example_value = parts[-1]
        value_type = "Integer"
        if '.' in example_value:
            value_type = "Float"
        elif 'yes' in example_value:
            value_type = "Boolean"
        modifier = {
            'id': mod_id,
            'value_type': value_type,
            'example': example,
            'description': cells[2].text,
            'effect_type': cells[3].text,
            'version_added': cells[4].text,
            'macro': []
        }

        parse_macro(modifier)
        modifiers[mod_id] = modifier

    with open("eu4_modifiers.json", "w") as data_file:
        json.dump(modifiers, data_file, indent=4, sort_keys=False)

    print(f"Parsed {len(modifiers)} modifiers")
