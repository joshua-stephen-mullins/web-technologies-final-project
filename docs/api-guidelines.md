# API Guidelines

## REST Conventions

- GET /api/items — list or search
- GET /api/items/{id} — get by ID
- POST /api/items — create
- PUT /api/items/{id} — update
- DELETE /api/items/{id} — delete

---

## Response Format

All responses are wrapped in a `Result` object (`team.projectpulse.system.Result`):

```json
{
  "flag": true,
  "code": 200,
  "message": "Find All Success",
  "data": {}
}
```

Fields:

- `flag` — `true` for success, `false` for failure
- `code` — application-level status code (see Status Codes below)
- `message` — human-readable response message
- `data` — response payload (object, array, or null)

---

## Status Codes

Defined in `team.projectpulse.system.StatusCode`:

| Code | Constant              | Meaning                              |
|------|-----------------------|--------------------------------------|
| 200  | SUCCESS               | Request succeeded                    |
| 400  | INVALID_ARGUMENT      | Bad request (e.g., invalid params)   |
| 401  | UNAUTHORIZED          | Authentication failed                |
| 403  | FORBIDDEN             | No permission                        |
| 404  | NOT_FOUND             | Resource not found                   |
| 409  | CONFLICT              | Resource already exists              |
| 423  | LOCKED                | Resource is locked                   |
| 500  | INTERNAL_SERVER_ERROR | Server internal error                |

---

## Error Handling

- Use `@RestControllerAdvice` to handle exceptions globally
- All exceptions should be caught and returned as `Result` objects
- Do not let raw stack traces leak to the client
- Example error response:

```json
{
  "flag": false,
  "code": 404,
  "message": "User not found with id 42",
  "data": null
}
```

---

## List Endpoints

All list endpoints return a plain array in the `data` field — no pagination wrapper.

- Clients may pass optional query parameters for filtering (e.g., `?name=spring`)
- Filtering is implemented via JPA Specifications on the backend
- Results are not paginated; the full matching list is returned

Example response:

```json
{
  "flag": true,
  "code": 200,
  "message": "Find All Success",
  "data": [
    { "id": 1, "name": "Spring 2026" },
    { "id": 2, "name": "Fall 2026" }
  ]
}
```

---

## Authentication

### Login

Send credentials as JSON to the login endpoint:

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "yourpassword"
}
```

A successful response returns a JWT token:

```json
{
  "flag": true,
  "code": 200,
  "message": "Login successful",
  "data": {
    "token": "<jwt>",
    "username": "user@example.com",
    "roles": "ROLE_ADMIN"
  }
}
```

### Authenticated Requests

Include the JWT token in the `Authorization` header on all subsequent requests:

```
Authorization: Bearer <token>
```

Tokens expire after 1 hour. The frontend stores the token in `localStorage` and attaches it automatically via the Axios interceptor in `axiosClient.ts`.

### Public Endpoints (no token required)

| Method | URL |
|--------|-----|
| POST | `/api/auth/login` |
| POST | `/api/users/register` |
| POST | `/api/users/forgot-password` |
| POST | `/api/users/reset-password` |

All other endpoints require a valid Bearer token.
