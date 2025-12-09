package com.okntscgl.secure_ai_notes_api.service;

import com.okntscgl.secure_ai_notes_api.model.Role;
import com.okntscgl.secure_ai_notes_api.model.User;
import com.okntscgl.secure_ai_notes_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public User updateUser(String id, User newUser) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setName(newUser.getName());
                    existing.setEmail(newUser.getEmail());
                    existing.setUsername(newUser.getUsername());
                    existing.setRole(newUser.getRole());
                    return userRepository.save(existing);
                })
                .orElse(null);
    }

    public boolean deleteUser(String id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
}
