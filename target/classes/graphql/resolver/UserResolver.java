package com.okntscgl.secure_ai_notes_api.graphql.resolver;

import com.okntscgl.secure_ai_notes_api.dto.LoginInput;
import com.okntscgl.secure_ai_notes_api.dto.RegisterInput;
import com.okntscgl.secure_ai_notes_api.dto.RefreshTokenInput;
import com.okntscgl.secure_ai_notes_api.dto.TokenPair;
import com.okntscgl.secure_ai_notes_api.model.NoteRequestModel;
import com.okntscgl.secure_ai_notes_api.model.Role;
import com.okntscgl.secure_ai_notes_api.model.User;
import com.okntscgl.secure_ai_notes_api.service.AiService;
import com.okntscgl.secure_ai_notes_api.service.UserService;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class UserResolver {

    private final UserService userService;
    private final AiService aiService;

    public UserResolver(UserService userService, AiService aiService) {
        this.userService = userService;
        this.aiService = aiService;
    }

    // ===================== QUERIES =====================
    @QueryMapping
    public List<User> getUsers() {
        requireRole(Role.ROLE_ADMIN, Role.ROLE_MANAGER);
        return userService.getAllUsers();
    }

    @QueryMapping
    public User getUserById(String id) {
        requireRole(Role.ROLE_ADMIN, Role.ROLE_MANAGER);
        return userService.getUserById(id).orElse(null);
    }

    @QueryMapping
    public List<NoteRequestModel> getNotes() {
        requireRole(Role.ROLE_ADMIN, Role.ROLE_MANAGER);
        return aiService.getAllNotes();
    }

    @QueryMapping
    public NoteRequestModel getNoteById(String id) {
        requireRole(Role.ROLE_ADMIN, Role.ROLE_MANAGER);
        return aiService.getNoteById(id).orElse(null);
    }

    @QueryMapping
    public List<NoteRequestModel> getNotesByUser(String userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("You cannot view notes of other users");
        }
        return aiService.getNotesByUser(userId);
    }

    // ===================== MUTATIONS =====================
    @MutationMapping
    public TokenPair register(RegisterInput input) {
        return userService.register(input);
    }

    @MutationMapping
    public TokenPair login(LoginInput input) {
        return userService.login(input);
    }

    @MutationMapping
    public TokenPair refreshToken(RefreshTokenInput input) {
        return userService.refreshToken(input.getRefreshToken());
    }

    @MutationMapping
    public NoteRequestModel createNote(String title, String content) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ROLE_USER) {
            throw new AccessDeniedException("Only USER role can create notes");
        }

        NoteRequestModel note = new NoteRequestModel();
        note.setUserId(currentUser.getId());
        note.setTitle(title);
        note.setContent(content);

        return aiService.createNote(note);
    }

    @MutationMapping
    public NoteRequestModel updateNote(String id, String title, String content) {
        User currentUser = getCurrentUser();
        NoteRequestModel note = aiService.getNoteById(id).orElseThrow(() ->
                new RuntimeException("Note not found")
        );

        if (!note.getUserId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("You cannot update this note");
        }

        if (title != null) note.setTitle(title);
        if (content != null) note.setContent(content);

        return aiService.updateNote(note);
    }

    @MutationMapping
    public Boolean deleteNote(String id) {
        User currentUser = getCurrentUser();
        NoteRequestModel note = aiService.getNoteById(id).orElseThrow(() ->
                new RuntimeException("Note not found")
        );

        if (!note.getUserId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("You cannot delete this note");
        }

        return aiService.deleteNote(id);
    }

    @MutationMapping
    public User updateUser(String id, String name, String email) {
        requireRole(Role.ROLE_ADMIN, Role.ROLE_MANAGER);

        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isEmpty()) return null;

        User user = optionalUser.get();
        if (name != null) user.setName(name);
        if (email != null) user.setEmail(email);

        return userService.updateUser(id, user);
    }

    @MutationMapping
    public Boolean deleteUser(String id) {
        requireRole(Role.ROLE_ADMIN, Role.ROLE_MANAGER);
        return userService.deleteUser(id);
    }

    // ===================== HELPERS =====================
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        throw new RuntimeException("Unauthorized");
    }

    private void requireRole(Role... roles) {
        User user = getCurrentUser();
        for (Role role : roles) {
            if (user.getRole() == role) return;
        }
        throw new AccessDeniedException("You do not have permission");
    }
}
