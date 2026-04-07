package com.oyewolz.apollofederationmultisubgraph.repository;

import java.util.List;

import com.oyewolz.apollofederationmultisubgraph.model.Author;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorRepository {

    private static final List<Author> AUTHORS = List.of(
            new Author("author-1", "Ada Lovelace"),
            new Author("author-2", "Octavia Butler"),
            new Author("author-3", "James Baldwin"));

    public List<Author> findAll() {
        return AUTHORS;
    }

    public Author findById(String id) {
        return AUTHORS.stream()
                .filter(author -> author.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}
