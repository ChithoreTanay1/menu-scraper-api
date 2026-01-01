# MenuScraper API

A Spring Boot REST API for collecting restaurant menu data from Python scrapers. It validates incoming data and stores it in PostgreSQL.

---

## Quick Start

```bash
# 1. Clone and run
git clone <repo>
cd menu-scraper-api
docker-compose up --build
```

```bash
# 2. Test it's working
curl http://localhost:8080/api/health
```

```bash
# 3. Post sample data
curl -X POST http://localhost:8080/api/menu-items/batch \
  -H "Content-Type: application/json" \
  -d '{
    "items": [{
      "restaurant_name": "Pizza Palace",
      "source_url": "https://pizza.palace.com/menu",
      "name": "Pepperoni Pizza",
      "price": 16.99,
      "currency": "USD"
    }]
  }'
```

```bash
# 4. Get data back
curl "http://localhost:8080/api/menu-items?restaurant=Pizza%20Palace"
```

---

## What This Does

**Python scrapers → Send JSON → MenuScraper API → PostgreSQL database**

The API acts as a clean, validated ingestion layer between web scrapers and persistent storage.

---

## Example Scraped Data

```json
{
  "restaurant_name": "Burger Joint",
  "source_url": "https://burger-joint.com/menu",
  "name": "Double Cheeseburger",
  "price": 12.99,
  "currency": "USD"
}
```

---

## API Endpoints

| Method | Endpoint                | Description                                      |
| ------ | ----------------------- | ------------------------------------------------ |
| POST   | `/api/menu-items/batch` | Upload multiple menu items                       |
| GET    | `/api/menu-items`       | Query items (filter by restaurant or source URL) |
| GET    | `/api/health`           | Check API and database health                    |

---

## Python Integration

```python
import requests

# Post scraped data
data = {
    "items": [{
        "restaurant_name": "Your Restaurant",
        "source_url": "https://your-restaurant.com",
        "name": "Menu Item",
        "price": 15.99,
        "currency": "USD"
    }]
}

response = requests.post(
    "http://localhost:8080/api/menu-items/batch",
    json=data
)

print(response.json())
```

---

## Run Without Docker

```bash
# 1. Start PostgreSQL
docker run -d -p 5432:5432 \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=menu_scraper \
  postgres:16-alpine
```

```bash
# 2. Run Spring Boot
mvn spring-boot:run
```

---

## Project Structure

```
src/main/java/com/menuscraper/
├── MenuScraperApplication.java
├── controller/           # API endpoints
├── service/              # Business logic
├── repository/           # Database access
├── entity/               # Database tables
└── dto/                  # Request/response formats
```

---

## Environment Variables

Set these in `application.yml` or as system environment variables:

```yaml
DB_URL: jdbc:postgresql://localhost:5432/menu_scraper
DB_USERNAME: postgres
DB_PASSWORD: postgres
SERVER_PORT: 8080
```

---

## Testing

```bash
# Run Python test script
python test_simple.py
```

```bash
# Or test with curl
curl http://localhost:8080/api/health
curl http://localhost:8080/api/menu-items
```

---

## Database Schema

```sql
-- Restaurants
CREATE TABLE restaurants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    source_url VARCHAR(500) UNIQUE NOT NULL
);

-- Menu Items
CREATE TABLE menu_items (
    id UUID PRIMARY KEY,
    restaurant_id UUID REFERENCES restaurants(id),
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    scraped_at TIMESTAMP
);
```

---

## Common Issues

### Port 8080 already in use

```bash
SERVER_PORT=8081 mvn spring-boot:run
```

### Database connection failed

```bash
# Check PostgreSQL is running
docker ps | grep postgres
```

```bash
# Or run with Docker Compose
docker-compose up
```

### Java version error

```bash
# Ensure Java 17+
java --version
```

```bash
# Or just use Docker
docker-compose up --build
```

---

## What's Next

* Add more menu item fields (allergens, calories, categories)
* Add authentication for multi-tenant scrapers
* Add analytics endpoints (price trends, popular items)
* Add webhook notifications for new data

---

## Tech Stack

* **Backend:** Spring Boot 4
* **Database:** PostgreSQL 16
* **Language:** Java 21
* **Infrastructure:** Docker & Docker Compose

---

## Use Case

Collect, normalize, and persist restaurant menu data from automated web scrapers.

---

## Status

**Production-ready**
