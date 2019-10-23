import requests
from bs4 import BeautifulSoup

url = "https://classificados.inf.ufsc.br/latestads.php"
r = requests.get(url)
soup = BeautifulSoup(r.text, "html.parser")
table = soup.find("table", attrs={"class": "box"}).find_all("tr")
for row in table:
    for result in row.find_all("td"):
        print(result.text)