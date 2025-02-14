import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {
    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void testSaveNewDocument() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Title")
                .content("Content")
                .author(DocumentManager.Author.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Author Name")
                        .build())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);
        assertNotNull(savedDocument.getId());
        assertNotNull(savedDocument.getCreated());
    }

    @Test
    void testFindById() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Find Test")
                .content("Findable content")
                .author(DocumentManager.Author.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Author Name")
                        .build())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);
        Optional<DocumentManager.Document> foundDocument = documentManager.findById(savedDocument.getId());

        assertTrue(foundDocument.isPresent());
        assertEquals("Find Test", foundDocument.get().getTitle());
    }

    @Test
    void testSearchByTitlePrefix() {
        DocumentManager.Document document1 = documentManager.save(DocumentManager.Document.builder()
                .title("Document One")
                .content("Content 1")
                .author(DocumentManager.Author.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Author Andrii")
                        .build())
                .build());

        DocumentManager.Document document2 = documentManager.save(DocumentManager.Document.builder()
                .title("Another Document")
                .content("Content 2")
                .author(DocumentManager.Author.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Author Yana")
                        .build())
                .build());

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Document"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);
        assertEquals(1, results.size());
        assertEquals("Document One", results.get(0).getTitle());
    }

    @Test
    void testSearchByAuthorId() {
        String authorId = UUID.randomUUID().toString();
        DocumentManager.Document document = documentManager.save(DocumentManager.Document.builder()
                .title("Unique Title")
                .content("Some content")
                .author(DocumentManager.Author.builder()
                        .id(authorId)
                        .name("Specific Author")
                        .build())
                .build());

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .authorIds(List.of(authorId))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);
        assertEquals(1, results.size());
        assertEquals("Unique Title", results.get(0).getTitle());
    }
}
