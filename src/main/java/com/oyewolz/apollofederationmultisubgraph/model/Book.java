package com.oyewolz.apollofederationmultisubgraph.model;

public record Book(
        String id,
        String title,
        String description,
        String genre,
        int publishedYear,
        int pageCount,
        String authorId) {
}
