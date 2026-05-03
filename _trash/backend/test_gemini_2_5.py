import urllib.request
import json

url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=AIzaSyCY_4V4BNufLDLIi48AYfWt8SeT98B84_8"
payload = {
    "contents": [
        {
            "parts": [
                {"text": "Hello, verify your existence."}
            ]
        }
    ]
}
data = json.dumps(payload).encode("utf-8")
headers = {"Content-Type": "application/json"}

req = urllib.request.Request(url, data=data, headers=headers)
try:
    with urllib.request.urlopen(req) as response:
        print(f"Status: {response.getcode()}")
        print(f"Body: {response.read().decode('utf-8')}")
except urllib.error.HTTPError as e:
    print(f"Status: {e.code}")
    print(f"Body: {e.read().decode('utf-8')}")
except Exception as e:
    print(f"Error: {e}")
