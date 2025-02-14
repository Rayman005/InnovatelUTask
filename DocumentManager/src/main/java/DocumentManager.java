import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class DocumentManager {

    private final Map<String, Document> storage = new ConcurrentHashMap<>();

    /**
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty()) {
            document = document.builder()
                    .id(UUID.randomUUID().toString())
                    .title(document.getTitle())
                    .content(document.getContent())
                    .author(document.getAuthor())
                    .created(Instant.now())
                    .build();
        }
        storage.put(document.getId(), document);
        return document;
    }


    /**
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(doc -> request.titlePrefixes == null ||
                        request.getTitlePrefixes().stream().anyMatch(prefix -> doc.getTitle().startsWith(prefix)))
                .filter(doc -> request.getContainsContents() == null ||
                        request.getContainsContents().stream().anyMatch(content -> doc.getContent().contains(content)))
                .filter(doc -> request.getAuthorIds() == null ||
                        request.getAuthorIds().contains(doc.getAuthor().getId()))
                .filter(doc -> (request.getCreatedFrom() == null || !doc.getCreated().isBefore(request.getCreatedFrom())) &&
                        (request.getCreatedTo() == null || !doc.getCreated().isAfter(request.getCreatedTo())))
                .collect(Collectors.toList());
    }

    /**
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Map<String, Document> getStorage() {
        return storage;
    }

    @Override
    public String toString() {
        return "DocumentManager{" +
                "storage=" + storage +
                '}';
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
    public static class Author {
        private String id;
        private String name;
    }
}