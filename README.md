# Apollo Federation Multi-Subgraph Demo

This project runs a single Spring Boot application that exposes two independent Apollo Federation subgraph endpoints:

- `books` at `http://localhost:8080/graphql/books`
- `authors` at `http://localhost:8080/graphql/authors`

Apollo Router composes those endpoints into one federated graph at `http://localhost:4000`.

## Domain model

- `Book` is owned by the `books` subgraph
- `Author` is owned by the `authors` subgraph
- `Book.author` returns an `Author` entity reference
- `Author.books` resolves the reverse relationship in the `books` subgraph
- the `authors` subgraph resolves that reference with `@EntityMapping`

The example deliberately keeps book and author details in different subgraphs:

- book details (`title`, `description`, `genre`, `publishedYear`, and `pageCount`) come from `books`
- author details (`name`, `country`, and `birthYear`) come from `authors`

Apollo Router stitches both parts together through the shared `Author.id` federation key. The client can therefore request one nested result without knowing which subgraph owns each field.

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
    description
    genre
    publishedYear
    pageCount
    author {
      id
      name
      country
      birthYear
    }
  }
}
```

The router first fetches each book and its author reference from the `books` subgraph. It then uses the returned author `id` to resolve the remaining author fields from the `authors` subgraph and combines them into the response.

## Query an author and their books

Federation also works in the opposite direction. `Author` is owned by the `authors` subgraph. The `books` subgraph extends that entity with `Author.books`, and the composed supergraph records that the field belongs to `books`:

```graphql
type Author @key(fields: "id") @extends {
  id: ID! @external
  books(genre: String): [Book!]!
}
```

Clients query the composed supergraph through Apollo Router:

```graphql
query AuthorsWithBooks {
  authors {
    id
    name
    country
    books {
      id
      title
      genre
      publishedYear
    }
  }
}
```

For this query, Apollo Router's supergraph query plan fetches the authors first, passes each `Author.id` to the `books` subgraph as an entity representation, and stitches the matching books onto each author. The client and the authors subgraph do not perform this join.

The extended field also accepts an optional, case-insensitive genre filter. For example, this returns every author while including only their computing books:

```graphql
query AuthorsWithComputingBooks {
  authors {
    id
    name
    country
    books(genre: "Computing") {
      id
      title
      genre
      publishedYear
    }
  }
}
```

Authors without a matching book are still returned with an empty `books` list.

### Intentional per-entity resolution

This proof of concept intentionally resolves `Author.books` once for each author representation:

```java
@SchemaMapping(typeName = "Author", field = "books")
public List<Book> books(AuthorReference author, @Argument String genre) {
    return bookRepository.findByAuthorId(author.id(), genre);
}
```

This has N+1 characteristics by design. It mirrors workflows where a parent identifier first produces several group identifiers, and an expensive calculation—such as investment performance—must then run independently and sequentially for each group. Apollo Router still coordinates the cross-subgraph entity fetch; the owning subgraph controls how each entity's calculated field is evaluated.
