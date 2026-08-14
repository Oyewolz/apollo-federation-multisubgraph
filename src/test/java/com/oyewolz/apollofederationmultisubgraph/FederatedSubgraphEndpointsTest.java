package com.oyewolz.apollofederationmultisubgraph;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FederatedSubgraphEndpointsTest {

    @LocalServerPort
    private int port;

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Test
    void booksSubgraphReturnsBooksAndAuthorReferences() throws Exception {
        String payload = """
                {
                  "query": "query { books { id title description genre publishedYear pageCount author { id } } }"
                }
                """;

        String response = postGraphQl("/graphql/books", payload);

        assertThat(response).contains("\"id\":\"book-1\"");
        assertThat(response).contains("\"title\":\"Federation in Action\"");
        assertThat(response).contains("\"description\":\"A practical introduction to composing GraphQL subgraphs.\"");
        assertThat(response).contains("\"genre\":\"Technology\"");
        assertThat(response).contains("\"publishedYear\":2024");
        assertThat(response).contains("\"pageCount\":320");
        assertThat(response).contains("\"author\":{\"id\":\"author-1\"}");
    }

    @Test
    void authorsSubgraphResolvesFederatedAuthorEntity() throws Exception {
        String payload = """
                {
                  "query": "query($representations: [_Any!]!) { _entities(representations: $representations) { ... on Author { id name country birthYear } } }",
                  "variables": {
                    "representations": [
                      {
                        "__typename": "Author",
                        "id": "author-2"
                      }
                    ]
                  }
                }
                """;

        String response = postGraphQl("/graphql/authors", payload);

        assertThat(response).contains("\"id\":\"author-2\"");
        assertThat(response).contains("\"name\":\"Octavia Butler\"");
        assertThat(response).contains("\"country\":\"United States\"");
        assertThat(response).contains("\"birthYear\":1947");
    }

    @Test
    void booksSubgraphResolvesBooksForFederatedAuthorEntity() throws Exception {
        String payload = """
                {
                  "query": "query($representations: [_Any!]!) { _entities(representations: $representations) { ... on Author { id books { id title } } } }",
                  "variables": {
                    "representations": [
                      {
                        "__typename": "Author",
                        "id": "author-2"
                      }
                    ]
                  }
                }
                """;

        String response = postGraphQl("/graphql/books", payload);

        assertThat(response).contains("\"id\":\"author-2\"");
        assertThat(response).contains("\"id\":\"book-2\",\"title\":\"Subgraph Patterns\"");
        assertThat(response).contains("\"id\":\"book-6\",\"title\":\"Kindred Futures\"");
        assertThat(response).contains("\"id\":\"book-7\",\"title\":\"Parable of the Stars\"");
        assertThat(response).contains("\"id\":\"book-8\",\"title\":\"Pattern Masters\"");
        assertThat(response.split("\"id\":\"book-", -1).length - 1).isEqualTo(4);
    }

    @Test
    void booksSubgraphFiltersFederatedAuthorBooksByGenre() throws Exception {
        String payload = """
                {
                  "query": "query($representations: [_Any!]!) { _entities(representations: $representations) { ... on Author { id books(genre: \\\"computing\\\") { id title genre } } } }",
                  "variables": {
                    "representations": [
                      {
                        "__typename": "Author",
                        "id": "author-1"
                      },
                      {
                        "__typename": "Author",
                        "id": "author-2"
                      }
                    ]
                  }
                }
                """;

        String response = postGraphQl("/graphql/books", payload);

        assertThat(response).contains("\"id\":\"author-1\"");
        assertThat(response).contains("\"id\":\"book-4\",\"title\":\"The Analytical Engine\",\"genre\":\"Computing\"");
        assertThat(response).contains("{\"id\":\"author-2\",\"books\":[]}");
        assertThat(response.split("\"id\":\"book-", -1).length - 1).isEqualTo(1);
    }

    private String postGraphQl(String path, String payload) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        return response.body();
    }
}
