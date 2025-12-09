package com.okntscgl.secure_ai_notes_api.service;

import com.okntscgl.secure_ai_notes_api.model.NoteRequestModel;
import com.okntscgl.secure_ai_notes_api.repository.NoteRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final NoteRepository noteRepository;

    public AiService(ChatClient.Builder builder, NoteRepository noteRepository) {
        this.chatClient = builder.build();
        this.noteRepository = noteRepository;
    }

    public String chat(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Query cannot be empty");
        }

        try {
            return chatClient
                    .prompt()
                    .user(query)
                    .call()
                    .content();
        } catch (Exception e) {
            throw new RuntimeException("AI service failed", e);
        }
    }

   
    public List<NoteRequestModel> getNotesByUser(String username) {
        return noteRepository.findByUserId(username);
    }


    public Optional<NoteRequestModel> getNoteById(String id) {
        return noteRepository.findById(id);
    }

    
    public NoteRequestModel createNote(NoteRequestModel note) {
        return noteRepository.save(note);
    }
}

