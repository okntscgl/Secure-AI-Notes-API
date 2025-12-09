package com.okntscgl.secure_ai_notes_api.repository;

import com.okntscgl.secure_ai_notes_api.model.NoteRequestModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends MongoRepository<NoteRequestModel, String> {
    List<NoteRequestModel> findByUserId(String userId);
}
