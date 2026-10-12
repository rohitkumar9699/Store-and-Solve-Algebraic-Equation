# Store and Solve Algebraic Equations

A Java 17 / Spring Boot REST API that stores algebraic expressions as postfix-built expression trees, reconstructs them for listing, and evaluates them against supplied variable values. Equation data is held in memory and is cleared when the application restarts.

The code is organized into conventional Spring packages under `FreightFox.StoreandSolveAlgebraicEquation`: `controller` handles HTTP requests, `service` contains application logic, `repository` defines and implements in-memory persistence, `exception` contains API error handling and domain exceptions, and `utility` contains expression parsing and tree evaluation.

## Run the application

```bash
./mvnw spring-boot:run
```

The API listens at `http://localhost:8080`.

## API

Interactive Swagger UI is available at `http://localhost:8080/swagger-ui/index.html` (the `/swagger-ui.html` URL redirects there). The OpenAPI document is at `http://localhost:8080/v3/api-docs`.

In GitHub Codespaces, start the application, open the **Ports** tab, forward port `8080`, and open the forwarded port in a browser with `/swagger-ui/index.html` appended to its URL.

### Store an equation

`POST /api/equations/store`

```json
{"equation":"3x + 2y - z"}
```

Returns HTTP `201 Created`:

```json
{"message":"Equation stored successfully","equationId":"1"}
```

### List equations

`GET /api/equations`

Returns equations reconstructed from the stored expression trees. Implicit multiplication is displayed explicitly, for example `3 * x + 2 * y - z`.

### Evaluate an equation

`POST /api/equations/{equationId}/evaluate`

```json
{"variables":{"x":2,"y":3,"z":1}}
```

The response includes the equation, variables, and calculated result. Missing variables, malformed expressions, unsupported operators, and non-finite results return HTTP `400` with a JSON `message`. Unknown equation IDs return HTTP `404`.

For the example equation and values above, the result is `11` (`3*2 + 2*3 - 1`).

Supports `+`, `-`, `^`, parentheses, integer constants, variables, unary minus, and implicit multiplication such as `3x` and `2(x + 1)`. Exponentiation is right-associative and binds more tightly than unary minus. Explicit `*` and `/` operators are not part of the assignment's supported equation syntax.

## Tests

```bash
./mvnw test
```

## Deployment

The application is ready to deploy as a Spring Boot service. A live deployment URL must be supplied by the deployment environment; this repository does not provision hosting.
