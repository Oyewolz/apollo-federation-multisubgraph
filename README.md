# Apollo Federation Multi-Subgraph Demo

This project runs a single Spring Boot application that exposes two independent Apollo Federation subgraph endpoints:

- `books` at `http://localhost:8080/graphql/books`
- `authors` at `http://localhost:8080/graphql/authors`

Apollo Router composes those endpoints into one federated graph at `http://localhost:4000`.

## Domain model

- `Book` is owned by the `books` subgraph
- `Author` is owned by the `authors` subgraph
- `Book.author` returns an `Author` entity reference
- the `authors` subgraph resolves that reference with `@EntityMapping`

## Run locally with Maven

```bash
./mvnw spring-boot:run
```

Subgraph endpoints:

- `POST /graphql/books`
- `POST /graphql/authors`

## Compose the supergraph

The repository includes a checked-in `router/supergraph.graphql`. If you change either subgraph schema, regenerate it with Rover:

```bash
npm_config_cache=/tmp/apollo-rover-cache \
  npx -p @apollo/rover rover supergraph compose --config router/supergraph.yaml \
  > router/supergraph.graphql
```

## Run with Docker Compose

```bash
docker compose up --build
```

Services:

- Spring app: `http://localhost:8080`
- Apollo Router: `http://localhost:4000`

The router container is built from the Apollo Router GitHub release tarball, so it does not depend on `ghcr.io` access.

## Example federated query

Run this against Apollo Router at `http://localhost:4000`:

```graphql
query DemoQuery {
  books {
    id
    title
    author {
      id
      name
    }
  }
}
```
