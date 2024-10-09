package org.example.managers;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DocumentManager {
    private static Map<String,Document> storage = new HashMap<>();
    public Document save(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }else {
            if(document.getId() == null || document.getId().isEmpty()){

               document.setId(UUID.randomUUID().toString());
            }
        }
        storage.put(document.getId(),document);
        return document;
    }


    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(document -> matchesSearchRequest(document,request))
                .collect(Collectors.toList());


    }


    public Optional<Document> findById(String id) {

        return Optional.ofNullable(storage.get(id));
    }
    private boolean matchesSearchRequest(Document document, SearchRequest request) {
        if (request == null) {
            return true;
        }
        boolean matches = true;

        if (request.getTitlePrefixes() != null && !request.getTitlePrefixes().isEmpty()) {
            matches &= request.getTitlePrefixes().stream()
                    .anyMatch(prefix -> document.getTitle().startsWith(prefix));
        }

        if (request.getContainsContents() != null && !request.getContainsContents().isEmpty()) {
            matches &= request.getContainsContents().stream()
                    .anyMatch(content -> document.getContent().contains(content));
        }

        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            matches &= request.getAuthorIds().contains(document.getAuthor().getId());
        }

        if (request.getCreatedFrom() != null) {
            matches &= document.getCreated().isAfter(request.getCreatedFrom());
        }

        if (request.getCreatedTo() != null) {
            matches &= document.getCreated().isBefore(request.getCreatedTo());
        }

        return matches;
    }
    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }
    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }
}
