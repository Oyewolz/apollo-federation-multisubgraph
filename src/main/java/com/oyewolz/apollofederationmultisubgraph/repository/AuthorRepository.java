package com.oyewolz.apollofederationmultisubgraph.repository;

import java.util.List;

import com.oyewolz.apollofederationmultisubgraph.model.Author;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorRepository {

    private static final List<Author> AUTHORS = List.of(
            new Author("author-1", "Ada Lovelace", "United Kingdom", 1815),
            new Author("author-2", "Octavia Butler", "United States", 1947),
            new Author("author-3", "James Baldwin", "United States", 1924));

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
