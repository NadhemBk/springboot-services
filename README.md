# Customer Order & Product Catalog System

A microservices-based proof of concept consisting of a **Product Catalog Service** and a **Customer Order Service**.

## How to Run

### Prerequisites
* Docker and Docker Compose installed.
* Ports **8080** (Order Service) and **8081** (Catalog Service) must be available.

### Start the System
In the root directory, run:
```bash
docker compose up
```

### Reachable Endpoints
* **Order Service:** `http://localhost:8080/customer-orders`
* **Catalog Service:** `http://localhost:8081/api/v1/products`
* **Swagger UI (Order):** `http://localhost:8080/swagger-ui.html`
* **H2 Consoles:** 
    * Catalog: `http://localhost:8081/h2-console` (JDBC: `jdbc:h2:mem:catalogdb`)
    * Order: `http://localhost:8080/h2-console` (JDBC: `jdbc:h2:mem:orderdb`)

### API Documentation & Tools
* **Order Service Swagger:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **Catalog Service Swagger:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

### Database Access (H2 Consoles)
Both services use H2 In-Memory databases. You can access the consoles at:
* **Catalog DB:** [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
  * JDBC URL: `jdbc:h2:mem:catalogdb`
  * User: `sa` | Password: `password`
* **Order DB:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  * JDBC URL: `jdbc:h2:mem:orderdb`
  * User: `sa` | Password: `password`

---

## What I Built
* **Product Catalog Service:** Ownership of products with a bulk-search API for efficient validation.
* **Customer Order Service:** Full lifecycle management (Draft -> Preview -> Submitted -> Confirmed).
* **Inter-Service Communication:** Synchronous validation via OpenFeign.
* **Persistence:** Independent H2 In-Memory databases with Flyway migrations.
* **Seed Data:** Catalog is pre-seeded via Flyway with `po-1`, `po-2`, and `po-3`.

---

## Decisions and Tradeoffs

### 1. State Machine & Validation
* **Decision:** State transition logic is encapsulated within the `OrderState` Enum and enforced in the `OrderService`.
* **Tradeoff:** I chose a "Service-driven" state machine over a library like Spring State Machine to keep the PoC lightweight. 
* **Rule:** Once an order is `SUBMITTED`, all fields except the `state` become immutable to ensure data integrity during the fulfillment process.

### 2. Idempotency Design
* **Decision:** Implemented using a unique `idempotency_key` column in the database.
* **Mechanism:** On `POST`, the service checks if the key exists. If the payload matches the existing record, it returns the original order with a `X-Idempotency-Replayed: true` header. If the payload differs, it returns `409 Conflict`.
* **Distinguishable Replays:** The service includes a custom header X-Idempotency-Replayed: true in the response when an existing order is returned for a repeated request, allowing clients to distinguish between fresh creations and replays.
* **Tradeoff:** This ensures "exactly-once" semantics even if the client retries due to network timeouts.

### 3. PATCH Semantics (JSON Merge Patch)
* **Decision:** Used `Map<String, Object>` in the Controller to distinguish between "field not present" and "field is null" (JSON Merge Patch semantics).
* **Validation:** Every PATCH request triggers a re-validation of `productOfferingIds` against the Catalog if the `orderItems` list is modified.

### 4. Inter-Service Communication & Resilience
* **Decision:** Used **OpenFeign** with a custom `ErrorDecoder`.
* **Catalog Unavailability:** If the Catalog is down or too slow, the Order Service returns `503 Service Unavailable`. This follows the "Fail-Fast" principle. Configured Feign timeouts prevent cascading failures
* **Efficiency:** Instead of N requests for N items, I implemented a `/search` POST endpoint in the Catalog to validate all unique IDs in a single round-trip, preventing N+1 network calls.

### 5. Data Modeling
* **UUIDs:** Used for Order IDs to prevent ID-enumeration attacks and to ensure unique identification across distributed systems.
* **BigDecimal:** Used for prices to avoid floating-point arithmetic precision errors.
* **Flyway:** Used for versioned migrations to ensure that Docker environments and local development always share the exact same schema.

### 6. Error Handling & Status Codes
* **400 Bad Request:** Used for invalid state transitions (`IllegalStateTransitionException`) or malformed JSON.
* **404 Not Found:** Returned when an order ID does not exist (`OrderNotFoundException`).
* **409 Conflict:** Specifically for idempotency key collisions with mismatched payloads.
* **422 Unprocessable Entity:** Used when the request is syntactically correct, but business rules fail (e.g., `productOfferingId` not found in catalog).
* **503 Service Unavailable:** Custom handling for downstream service timeouts or outages (`CatalogUnavailableException`).

### 7. Consistent Error Contract
All services follow a unified error response shape. For validation errors, the contract is extended to provide specific details:

```json
{
  "timestamp": "ISO-8601-Timestamp",
  "status": 422,
  "errorCode": "INVALID_PRODUCT_IDS",
  "message": "Some products in your request do not exist.",
  "missingIds": ["po-10", "po-11"] // Extra field for pinpointing errors
}
```

---

## Assumptions & Limitations
* **Inventory:** This PoC assumes infinite stock; it only validates the existence of the `productOfferingId`.
* **Security:** No authentication layer (Spring Security) was implemented to focus on the core business requirements.
* **Cleanup:** Since H2 In-Memory is used, data is wiped when containers are stopped.

---
