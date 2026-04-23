### Student Details
* **Name:** Navidu Manathunga
* **Student ID:** W2121909
* **Coursework Title:** Smart Campus Sensor & Room Management API
* **GitHub Report:** https://github.com/navidumanathunga/smart-campus-rest-api
# Smart Campus Sensor & Room Management API

This project is a RESTful API built with JAX-RS (Jersey) and Grizzly HTTP Server.

## Architecture & Setup
The API is designed using Java and Jakarta RESTful Web Services (JAX-RS). It uses an in-memory database to store state. The base URL path for the API is `/api/v1`.

### Build & Run Instructions
1. Ensure you have Java 11+ and Maven installed.
2. Clone the repository.
3. Open a terminal and navigate to the project directory.
4. Run the following command to clean, compile, and execute the server:
   `mvn clean compile exec:java`
5. The server will start on `http://localhost:8080/`.

### Sample Curl Commands
1. **Discovery Endpoint:**
   `curl -X GET http://localhost:8080/api/v1`
2. **Create a Room:**
   `curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d '{"name": "Library", "capacity": 50}'`
3. **Register a Sensor:**
   `curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d '{"type": "CO2", "status": "ACTIVE", "roomId": "<insert_room_id>"}'`
4. **Append a Sensor Reading:**
   `curl -X POST http://localhost:8080/api/v1/sensors/<insert_sensor_id>/readings -H "Content-Type: application/json" -d '{"value": 450.5}'`
5. **Get Sensor Readings:**
   `curl -X GET http://localhost:8080/api/v1/sensors/<insert_sensor_id>/readings`

## Report Questions

### Part 1: Service Architecture & Setup
**Question:** In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.
**Answer:** The default lifecycle of a JAX-RS Resource class is request-scoped, meaning a new instance of the resource class is created for every incoming HTTP request and destroyed once the response is sent. This prevents resource classes from carrying over state between requests. However, when we use an in-memory database (like maps or lists in a singleton class or static fields), these structures are shared across all resource instances and multiple concurrent requests (threads). To prevent race conditions and data corruption, we must use thread-safe data structures such as `ConcurrentHashMap` or synchronize access to `ArrayList`. In this project, a Singleton `InMemoryDatabase` utilizing `ConcurrentHashMap` guarantees that concurrent GET, POST, and DELETE requests are managed securely without race conditions.

**Question:** Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?
**Answer:** Hypermedia as the Engine of Application State (HATEOAS) is the highest level of REST maturity. By providing navigable links directly within API responses, the server dictates the valid state transitions. This decouples the client from hardcoded URIs and static structures, allowing the API to evolve without breaking clients (as long as link relations remain consistent). It provides a self-documenting interface where clients can dynamically discover actions and resources, significantly reducing reliance on out-of-band, often outdated static documentation.

### Part 2: Room Management
**Question:** When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.
**Answer:** Returning only IDs drastically reduces the payload size, saving network bandwidth and latency, especially when dealing with thousands of rooms. However, it shifts the burden of fetching details to the client, requiring multiple subsequent GET requests (the N+1 query problem), which increases overhead and processing time. Conversely, returning full objects optimizes client-side processing as all required data is readily available in a single request, but it consumes more bandwidth and may transfer unnecessary data if the client only needs a summary view.

**Question:** Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.
**Answer:** Yes, the DELETE operation is idempotent. Idempotency guarantees that executing a request multiple times yields the same state on the server as executing it once. In our implementation, the first DELETE request for an existing room removes it and returns a 204 No Content. If the exact same DELETE request is sent again, the room will not be found (since it was already removed), and the server will safely return a 404 Not Found without modifying any server state. The server state remains identical after one or N identical DELETE requests.

### Part 3: Sensor Operations & Linking
**Question:** We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?
**Answer:** By explicitly declaring `@Consumes(MediaType.APPLICATION_JSON)`, the API strictly enforces that the endpoint only processes JSON payloads. If a client attempts to send data in `text/plain` or `application/xml`, the JAX-RS runtime intercepts the request before it even reaches the resource method and automatically returns an HTTP 415 Unsupported Media Type error. This prevents parsing errors and guarantees the resource method logic is executed only with the expected media type.

**Question:** You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?
**Answer:** URL Path parameters (`@PathParam`) are typically used to identify specific, unique resources (e.g., a specific sensor by ID). Query parameters (`@QueryParam`) are designed for filtering, sorting, or paginating a collection of resources. Using query parameters is superior for searching because they are inherently optional, easily composable (e.g., filtering by multiple criteria like `?type=CO2&status=ACTIVE`), and accurately reflect the RESTful principle that we are querying a single collection (`/sensors`), not defining an entirely new resource hierarchy based on arbitrary attributes.

### Part 4: Deep Nesting with Sub-Resources
**Question:** Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?
**Answer:** The Sub-Resource Locator pattern allows a parent resource to delegate the handling of nested URI segments to a distinct, separate class (e.g., `SensorReadingResource`). This enforces the Single Responsibility Principle, preventing the `SensorResource` class from becoming a massive, monolithic controller ("God class"). It drastically improves maintainability, readability, and encapsulation, as the logic and context (like `sensorId`) are neatly modularized. It also promotes code reusability if sub-resources are shared across different contexts.

### Part 5: Advanced Error Handling, Exception Mapping & Logging
**Question:** Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?
**Answer:** An HTTP 404 Not Found implies that the target URI itself does not map to an existing resource. In contrast, an HTTP 422 Unprocessable Entity indicates that the server understands the content type and syntax of the request entity, but was unable to process the contained instructions. When a client POSTs a valid JSON payload that includes an invalid foreign key reference (like a non-existent `roomId`), the endpoint exists and the JSON is valid, but the business constraint prevents processing. Thus, 422 is semantically accurate.

**Question:** From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?
**Answer:** Exposing internal Java stack traces is a significant security vulnerability (Information Disclosure). An attacker can harvest sensitive internal details, including the specific framework and library versions being used (enabling them to search for known CVEs), internal file paths and directory structures, and database or third-party service connection details. Stack traces also map the application's internal execution flow, giving attackers insights into potential logic flaws, unhandled edge cases, or injection points.

**Question:** Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?
**Answer:** JAX-RS filters provide a centralized, aspect-oriented approach to intercepting requests and responses globally. Placing logging logic inside filters decouples it from the core business logic in resource methods, adhering to the DRY (Don't Repeat Yourself) principle. It ensures consistent, comprehensive observability across the entire API without cluttering resource code. Furthermore, filters have access to lower-level context (like HTTP headers and generic URIs) and guarantee execution even if an exception occurs or a request is aborted early.
