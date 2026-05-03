import urllib.request
import json

url = "https://generativelanguage.googleapis.com/v1beta/models?key=AIzaSyCY_4V4BNufLDLIi48AYfWt8SeT98B84_8"

req = urllib.request.Request(url)
try:
    with urllib.request.urlopen(req) as response:
        print(f"Status: {response.getcode()}")
        models = json.loads(response.read().decode('utf-8'))
        for model in models.get('models', []):
            print(f"- {model['name']} ({model['supportedGenerationMethods']})")
except urllib.error.HTTPError as e:
    print(f"Status: {e.code}")
    print(f"Body: {e.read().decode('utf-8')}")
except Exception as e:
    print(f"Error: {e}")
