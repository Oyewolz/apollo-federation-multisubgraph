package com.oyewolz.apollofederationmultisubgraph.controller;

import java.util.List;

import com.oyewolz.apollofederationmultisubgraph.config.AuthorsSubgraph;
import com.oyewolz.apollofederationmultisubgraph.model.Author;
import com.oyewolz.apollofederationmultisubgraph.repository.AuthorRepository;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;

@AuthorsSubgraph
public class AuthorsSubgraphController {

    private final AuthorRepository authorRepository;

    public AuthorsSubgraphController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @QueryMapping
    public List<Author> authors() {
        return authorRepository.findAll();
    }

    @QueryMapping
    public Author authorById(@Argument String id) {
        return authorRepository.findById(id);
    }

    @EntityMapping
    public Author author(@Argument String id) {
        return authorRepository.findById(id);
    }
}
