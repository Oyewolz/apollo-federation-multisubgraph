package com.oyewolz.apollofederationmultisubgraph.repository;

import java.util.List;

import com.oyewolz.apollofederationmultisubgraph.model.Book;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepository {

    private static final List<Book> BOOKS = List.of(
            new Book(
                    "book-1",
                    "Federation in Action",
                    "A practical introduction to composing GraphQL subgraphs.",
                    "Technology",
                    2024,
                    320,
                    "author-1"),
            new Book(
                    "book-2",
                    "Subgraph Patterns",
                    "Reusable patterns for designing federated GraphQL services.",
                    "Technology",
                    2023,
                    284,
                    "author-2"),
            new Book(
                    "book-3",
                    "Graph Routing Essentials",
                    "An overview of query planning and routing across subgraphs.",
                    "Technology",
                    2025,
                    256,
                    "author-3"),
            new Book(
                    "book-4",
                    "The Analytical Engine",
                    "Ideas about computation, algorithms, and mechanical calculation.",
                    "Computing",
                    1843,
                    192,
                    "author-1"),
            new Book(
                    "book-5",
                    "Notes on Numbers",
                    "A collection of notes exploring numbers and symbolic operations.",
                    "Mathematics",
                    1844,
                    176,
                    "author-1"),
            new Book(
                    "book-6",
                    "Kindred Futures",
                    "A journey through time, identity, and inherited history.",
                    "Science Fiction",
                    1979,
                    264,
                    "author-2"),
            new Book(
                    "book-7",
                    "Parable of the Stars",
                    "Communities rebuild while searching for hope among the stars.",
                    "Science Fiction",
                    1993,
                    352,
                    "author-2"),
            new Book(
                    "book-8",
                    "Pattern Masters",
                    "Telepathic families struggle over power and human connection.",
                    "Science Fiction",
                    1976,
                    224,
                    "author-2"),
            new Book(
                    "book-9",
                    "Notes of a Native Voice",
                    "Essays about identity, belonging, and society.",
                    "Essays",
                    1955,
                    208,
                    "author-3"),
            new Book(
                    "book-10",
                    "The Fire Within",
                    "Reflections on history, faith, and social change.",
                    "Essays",
                    1963,
                    128,
                    "author-3"),
            new Book(
                    "book-11",
                    "Another Country Road",
                    "Friends confront love, loss, and identity in a changing city.",
                    "Literary Fiction",
                    1962,
                    448,
                    "author-3"),
            new Book(
                    "book-12",
                    "Giovanni's Journey",
                    "A story of love, self-discovery, and difficult choices.",
                    "Literary Fiction",
                    1956,
                    176,
                    "author-3"),
            new Book(
                    "book-13",
                    "Blues for the City",
                    "A family searches for redemption through music and community.",
                    "Literary Fiction",
                    1953,
                    240,
                    "author-3"));

    public List<Book> findAll() {
        return BOOKS;
    }

    public Book findById(String id) {
        return BOOKS.stream()
                .filter(book -> book.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Book> findByAuthorId(String authorId, String genre) {
        return BOOKS.stream()
                .filter(book -> book.authorId().equals(authorId))
                .filter(book -> genre == null || book.genre().equalsIgnoreCase(genre))
                .toList();
    }
}
