from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait

import json
from scrape_utils import parse_macro

with webdriver.Chrome() as driver:
    wait = WebDriverWait(driver, 10)
    driver.get('https://eu4.paradoxwikis.com/Scopes')
    rows = driver.find_elements(By.XPATH, '//table[./thead/tr/th[6][contains(text(),"Version")]]/tbody/tr')
    scopes = {}
    for row in rows:
        cells = row.find_elements(By.TAG_NAME, 'td')
        scope_id = cells[0].text
        scope = {
            'id': scope_id,
            'example': cells[1].text,
            'description': cells[2].text,
            'multiple_scopes': 'Yes' in cells[3].text,
            'changes_scope_to': cells[4].text,
            'version_added': cells[5].text,
            'macro': []
        }

        parse_macro(scope)
        scopes[scope_id] = scope

    with open("eu4_scopes.json", "w") as data_file:
        json.dump(scopes, data_file, indent=4, sort_keys=False)

    print(f"Parsed {len(scopes)} scopes")
