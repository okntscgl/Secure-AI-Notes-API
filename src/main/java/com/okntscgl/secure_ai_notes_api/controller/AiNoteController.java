package com.okntscgl.secure_ai_notes_api.controller;

import com.okntscgl.secure_ai_notes_api.model.NoteRequestModel;
import com.okntscgl.secure_ai_notes_api.service.AiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai-notes")
public class AiNoteController {

    private final AiService aiService;

    public AiNoteController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateNote(@Valid @RequestBody NoteRequestModel request) {
        String prompt = """
                You are a helpful assistant.
                Generate a secure technical note.

                Title: %s
                Content: %s
                """.formatted(request.getTitle(), request.getContent());

        String result = aiService.chat(prompt);
        return ResponseEntity.ok(result);
    }

    // Kullanıcının kendi notlarını çekmek için
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserNotes(@PathVariable String username) {
        return ResponseEntity.ok(aiService.getNotesByUser(username));
    }

    // Yeni not eklemek için
    @PostMapping("/user")
    public ResponseEntity<NoteRequestModel> createNote(@RequestBody NoteRequestModel note,
                                                       @RequestParam String username) {
        note.setUserId(username);
        return ResponseEntity.ok(aiService.createNote(note));
    }
}
