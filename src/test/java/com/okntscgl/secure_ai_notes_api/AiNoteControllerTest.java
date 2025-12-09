package com.okntscgl.secure_ai_notes_api;

import com.okntscgl.secure_ai_notes_api.controller.AiNoteController;
import com.okntscgl.secure_ai_notes_api.model.NoteRequestModel;
import com.okntscgl.secure_ai_notes_api.service.AiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AiNoteControllerTest {

    private AiService aiService;
    private AiNoteController aiNoteController;

    @BeforeEach
    void setUp() {
        aiService = mock(AiService.class);
        aiNoteController = new AiNoteController(aiService);
    }

    @Test
    void testGenerateNote() {
        NoteRequestModel request = new NoteRequestModel();
        request.setTitle("Test Title");
        request.setContent("Test Content");

        String mockedResponse = "Generated Note";
        when(aiService.chat(anyString())).thenReturn(mockedResponse);

        ResponseEntity<String> response = aiNoteController.generateNote(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockedResponse, response.getBody());

        verify(aiService, times(1)).chat(anyString());
    }

    @Test
    void testGetUserNotes() {
        String username = "testUser";
        List<NoteRequestModel> mockedNotes = List.of(new NoteRequestModel());
        when(aiService.getNotesByUser(username)).thenReturn(mockedNotes);

        ResponseEntity<?> response = aiNoteController.getUserNotes(username);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockedNotes, response.getBody());

        verify(aiService, times(1)).getNotesByUser(username);
    }

    @Test
    void testCreateNote() {
        String username = "testUser";
        NoteRequestModel note = new NoteRequestModel();
        note.setTitle("Title");
        note.setContent("Content");

        NoteRequestModel createdNote = new NoteRequestModel();
        createdNote.setTitle("Title");
        createdNote.setContent("Content");
        createdNote.setUserId(username);

        when(aiService.createNote(any(NoteRequestModel.class))).thenReturn(createdNote);

        ResponseEntity<NoteRequestModel> response = aiNoteController.createNote(note, username);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(createdNote, response.getBody());
        assertEquals(username, response.getBody().getUserId());

        verify(aiService, times(1)).createNote(any(NoteRequestModel.class));
    }
}

