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
                  "query": "query { books { id title author { id } } }"
                }
                """;

        String response = postGraphQl("/graphql/books", payload);

        assertThat(response).contains("\"id\":\"book-1\"");
        assertThat(response).contains("\"title\":\"Federation in Action\"");
        assertThat(response).contains("\"author\":{\"id\":\"author-1\"}");
    }

    @Test
    void authorsSubgraphResolvesFederatedAuthorEntity() throws Exception {
        String payload = """
                {
                  "query": "query($representations: [_Any!]!) { _entities(representations: $representations) { ... on Author { id name } } }",
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
