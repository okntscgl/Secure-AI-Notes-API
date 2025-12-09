package com.okntscgl.secure_ai_notes_api.repository;

import com.okntscgl.secure_ai_notes_api.model.Role;
import com.okntscgl.secure_ai_notes_api.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);
}
