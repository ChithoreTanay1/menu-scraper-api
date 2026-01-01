import requests
import json

# Configuration
BASE_URL = "http://localhost:8080"

print("="*60)
print("TESTING MENU SCRAPER API")
print("="*60)

# 1. Health Check
print("\n1️⃣  Testing Health Endpoint...")
health_response = requests.get(f"{BASE_URL}/api/health")
print(f"   Status: {health_response.status_code}")
if health_response.status_code == 200:
    health_data = health_response.json()
    print(f"   API Status: {health_data['status']}")
    print(f"   Database: {health_data['database']['status']}")

# 2. Post Sample Menu Items
print("\n2️⃣  Posting Sample Menu Items...")
sample_data = {
    "items": [
        {
            "restaurant_name": "Pizza Palace",
            "source_url": "https://pizza.palace.com/menu",
            "name": "Pepperoni Pizza",
            "description": "Classic pepperoni with mozzarella cheese",
            "price": 16.99,
            "currency": "USD"
        },
        {
            "restaurant_name": "Pizza Palace",
            "source_url": "https://pizza.palace.com/menu",
            "name": "Margherita Pizza",
            "description": "Fresh tomatoes, mozzarella, basil",
            "price": 14.50,
            "currency": "USD"
        },
        {
            "restaurant_name": "Sushi Zen",
            "source_url": "https://sushi.zen.jp/tokyo",
            "name": "Salmon Nigiri",
            "description": "2 pieces of fresh salmon on rice",
            "price": 6.50,
            "currency": "JPY"
        }
    ]
}

batch_response = requests.post(
    f"{BASE_URL}/api/menu-items/batch",
    json=sample_data,
    headers={"Content-Type": "application/json"}
)

print(f"   Batch POST Status: {batch_response.status_code}")
if batch_response.status_code == 200:
    batch_result = batch_response.json()
    print(f"   ✅ Saved {batch_result['saved_count']} out of {batch_result['total_requested']} items")
else:
    print(f"   ❌ Error: {batch_response.json()}")

# 3. Query All Items
print("\n3️⃣  Querying All Menu Items...")
query_response = requests.get(f"{BASE_URL}/api/menu-items")
print(f"   GET Status: {query_response.status_code}")
if query_response.status_code == 200:
    items = query_response.json()
    print(f"   📋 Found {len(items)} menu items:")
    for i, item in enumerate(items, 1):
        print(f"      {i}. {item['restaurant_name']} - {item['name']}")
        print(f"         Price: {item['price']} {item['currency']}")
        print(f"         Description: {item['description'][:50]}...")

# 4. Query by Restaurant
print("\n4️⃣  Querying by Restaurant Name...")
restaurant_query = requests.get(
    f"{BASE_URL}/api/menu-items",
    params={"restaurant": "Pizza Palace"}
)
print(f"   Query Status: {restaurant_query.status_code}")
if restaurant_query.status_code == 200:
    pizza_items = restaurant_query.json()
    print(f"   🍕 Found {len(pizza_items)} items from Pizza Palace")

# 5. Test Invalid Data
print("\n5️⃣  Testing Validation (Should Fail)...")
invalid_data = {
    "items": [{
        "restaurant_name": "",  # Empty - should fail
        "source_url": "https://test.com",
        "name": "Test Item",
        "price": -5.99,  # Negative - should fail
        "currency": "XYZ"  # Invalid - should fail
    }]
}

invalid_response = requests.post(
    f"{BASE_URL}/api/menu-items/batch",
    json=invalid_data
)
print(f"   Invalid POST Status: {invalid_response.status_code}")
if invalid_response.status_code == 400:
    print(f"   ✅ Correctly rejected invalid data (as expected)")

print("\n" + "="*60)
print("TEST COMPLETE! 🎉")
print("="*60)