package com.oyewolz.apollofederationmultisubgraph.repository;

import java.util.List;

import com.oyewolz.apollofederationmultisubgraph.model.Book;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepository {

    private static final List<Book> BOOKS = List.of(
            new Book("book-1", "Federation in Action", "author-1"),
            new Book("book-2", "Subgraph Patterns", "author-2"),
            new Book("book-3", "Graph Routing Essentials", "author-3"));

    public List<Book> findAll() {
        return BOOKS;
    }

    public Book findById(String id) {
        return BOOKS.stream()
                .filter(book -> book.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}
