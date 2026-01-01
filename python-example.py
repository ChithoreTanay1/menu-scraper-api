import requests
import json
from datetime import datetime

# Configuration
API_BASE_URL = "http://localhost:8080/api"

def post_batch_menu_items():
    """Example of posting scraped menu data to the API"""

    # Sample scraped data
    menu_items = {
        "items": [
            {
                "restaurant_name": "La Trattoria",
                "source_url": "https://example.com/la-trattoria",
                "name": "Spaghetti Carbonara",
                "description": "Classic Roman pasta with eggs, cheese, pancetta, and black pepper",
                "price": 18.50,
                "currency": "EUR"
            },
            {
                "restaurant_name": "La Trattoria",
                "source_url": "https://example.com/la-trattoria",
                "name": "Margherita Pizza",
                "description": "Tomato sauce, fresh mozzarella, basil",
                "price": 14.00,
                "currency": "EUR"
            },
            {
                "restaurant_name": "Sushi Palace",
                "source_url": "https://example.com/sushi-palace",
                "name": "Salmon Nigiri",
                "description": "2 pieces of fresh salmon on rice",
                "price": 6.50,
                "currency": "USD"
            },
            {
                "restaurant_name": "Sushi Palace",
                "source_url": "https://example.com/sushi-palace",
                "name": "Dragon Roll",
                "description": "Eel, cucumber, avocado with eel sauce",
                "price": 15.75,
                "currency": "USD"
            },
            {
                "restaurant_name": "Burger Joint",
                "source_url": "https://example.com/burger-joint",
                "name": "Classic Cheeseburger",
                "description": "Beef patty, cheddar, lettuce, tomato, special sauce",
                "price": 12.99,
                "currency": "USD"
            }
        ]
    }

    try:
        response = requests.post(
            f"{API_BASE_URL}/menu-items/batch",
            json=menu_items,
            headers={"Content-Type": "application/json"}
        )

        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.json()}")

        return response.json()

    except requests.exceptions.RequestException as e:
        print(f"Error making request: {e}")
        return None

def query_menu_items(restaurant_name=None, source_url=None):
    """Query menu items by restaurant name or source URL"""

    params = {}
    if restaurant_name:
        params["restaurant"] = restaurant_name
    if source_url:
        params["source_url"] = source_url

    try:
        response = requests.get(
            f"{API_BASE_URL}/menu-items",
            params=params
        )

        print(f"Status Code: {response.status_code}")

        if response.status_code == 200:
            items = response.json()
            print(f"Found {len(items)} menu items")
            for item in items:
                print(f"- {item['name']}: ${item['price']} {item['currency']}")

            return items
        else:
            print(f"Error: {response.json()}")

    except requests.exceptions.RequestException as e:
        print(f"Error making request: {e}")

    return None

def check_health():
    """Check API health status"""

    try:
        response = requests.get(f"{API_BASE_URL}/health")
        print(f"Health Status: {response.json()}")
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"Error checking health: {e}")
        return None

if __name__ == "__main__":
    print("=== Testing Menu Scraper API ===\n")

    # 1. Check health
    print("1. Checking API health...")
    check_health()

    print("\n" + "="*50 + "\n")

    # 2. Post batch data
    print("2. Posting batch menu items...")
    batch_result = post_batch_menu_items()

    print("\n" + "="*50 + "\n")

    # 3. Query by restaurant name
    print("3. Querying menu items for 'Sushi Palace'...")
    query_menu_items(restaurant_name="Sushi Palace")

    print("\n" + "="*50 + "\n")

    # 4. Query by source URL
    print("4. Querying menu items by source URL...")
    query_menu_items(source_url="https://example.com/la-trattoria")