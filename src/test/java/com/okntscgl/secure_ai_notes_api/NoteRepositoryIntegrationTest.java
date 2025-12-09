package com.okntscgl.secure_ai_notes_api;

import com.okntscgl.secure_ai_notes_api.model.NoteRequestModel;
import com.okntscgl.secure_ai_notes_api.repository.NoteRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NoteRepositoryIntegrationTest {

    @Autowired
    private NoteRepository noteRepository;

    @Test
    @Order(1)
    void testSaveNote() {
        NoteRequestModel note = new NoteRequestModel(
                "user123",
                "First Note",
                "Content of first note"
        );

        NoteRequestModel saved = noteRepository.save(note);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("user123", saved.getUserId());
        assertEquals("First Note", saved.getTitle());
    }

    @Test
    @Order(2)
    void testFindByUserId() {
        List<NoteRequestModel> notes = noteRepository.findByUserId("user123");

        assertFalse(notes.isEmpty());
        assertTrue(notes.stream().anyMatch(n -> n.getTitle().equals("First Note")));
    }

    @AfterAll
    static void cleanUp(@Autowired NoteRepository noteRepository) {
        noteRepository.deleteAll();
    }
}
