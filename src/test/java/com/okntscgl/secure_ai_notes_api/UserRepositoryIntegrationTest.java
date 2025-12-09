package com.okntscgl.secure_ai_notes_api;

import com.okntscgl.secure_ai_notes_api.model.Role;
import com.okntscgl.secure_ai_notes_api.model.User;
import com.okntscgl.secure_ai_notes_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection("users");

        User user1 = new User();
        user1.setUsername("john");
        user1.setPassword("123");
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setRole(Role.ROLE_USER);

        User user2 = new User();
        user2.setUsername("admin");
        user2.setPassword("123");
        user2.setName("Admin Boss");
        user2.setEmail("admin@example.com");
        user2.setRole(Role.ROLE_ADMIN);

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    void testFindByUsername() {
        Optional<User> user = userRepository.findByUsername("john");

        assertTrue(user.isPresent());
        assertEquals("john@example.com", user.get().getEmail());
    }

    @Test
    void testExistsByEmail() {
        boolean exists = userRepository.existsByEmail("john@example.com");

        assertTrue(exists);
    }

    @Test
    void testFindByEmail() {
        Optional<User> user = userRepository.findByEmail("admin@example.com");

        assertTrue(user.isPresent());
        assertEquals("admin", user.get().getUsername());
    }

    @Test
    void testFindByRole() {
        List<User> admins = userRepository.findByRole(Role.ROLE_ADMIN);

        assertEquals(1, admins.size());
        assertEquals("admin", admins.get(0).getUsername());
    }
}
