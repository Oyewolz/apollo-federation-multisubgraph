package com.oyewolz.apollofederationmultisubgraph.controller;

import java.util.List;

import com.oyewolz.apollofederationmultisubgraph.config.BooksSubgraph;
import com.oyewolz.apollofederationmultisubgraph.model.AuthorReference;
import com.oyewolz.apollofederationmultisubgraph.model.Book;
import com.oyewolz.apollofederationmultisubgraph.repository.BookRepository;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

@BooksSubgraph
public class BooksSubgraphController {

    private final BookRepository bookRepository;

    public BooksSubgraphController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @QueryMapping
    public List<Book> books() {
        return bookRepository.findAll();
    }

    @QueryMapping
    public Book bookById(@Argument String id) {
        return bookRepository.findById(id);
    }

    @EntityMapping
    public Book book(@Argument String id) {
        return bookRepository.findById(id);
    }

    @SchemaMapping(typeName = "Book", field = "author")
    public AuthorReference author(Book book) {
        return new AuthorReference(book.authorId());
    }
}
