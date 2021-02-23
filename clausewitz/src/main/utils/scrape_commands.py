from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait

import json
from scrape_utils import parse_macro

with webdriver.Chrome() as driver:
    wait = WebDriverWait(driver, 10)
    driver.get('https://eu4.paradoxwikis.com/Commands')
    rows = driver.find_elements(By.XPATH, '//table[./thead/tr/th[6][contains(text(),"Version")]]/tbody/tr')
    commands = {}
    for row in rows:
        cells = row.find_elements(By.TAG_NAME, 'td')
        cmd_id = cells[0].text
        parameters = cells[1].text
        example = cells[2].text
        parts = example.split(' = ')
        example_value = parts[-1]
        value_type = "Integer"
        if '.' in example_value:
            value_type = "Float"
        elif 'yes' in example_value:
            value_type = "Boolean"
        command = {
            'id': cmd_id,
            'parameters': parameters,
            'value_type': value_type,
            'example': example,
            'description': cells[3].text,
            'notes': cells[4].text,
            'version_added': cells[5].text if 5 < len(cells) else None,
            'macro': []
        }

        parse_macro(command)
        commands[cmd_id] = command

    with open("eu4_commands.json", "w") as data_file:
        json.dump(commands, data_file, indent=4, sort_keys=False)

    print(f"Parsed {len(commands)} commands")
