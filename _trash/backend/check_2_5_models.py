import urllib.request
import json

url = "https://generativelanguage.googleapis.com/v1beta/models?key=AIzaSyCY_4V4BNufLDLIi48AYfWt8SeT98B84_8"

req = urllib.request.Request(url)
try:
    with urllib.request.urlopen(req) as response:
        models = json.loads(response.read().decode('utf-8'))
        for m in models.get('models', []):
            if '2.5' in m['name']:
                print(f"{m['name']} {m.get('supportedGenerationMethods', [])}")
except Exception as e:
    print(f"Error: {e}")
