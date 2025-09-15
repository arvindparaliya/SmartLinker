package com.smartlinker.services;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;
import com.smartlinker.entities.User;

public interface UserService {

    // --------------------------
    // Registration
    // --------------------------
    User saveUser(User user);          // Register new user (with encoding + email verification)
    User registerUser(User user);      // Wrapper for saveUser with duplicate email check

    // --------------------------
    // Retrieval
    // --------------------------
    Optional<User> getUserById(String id);
    List<User> getAllUsers();
    User getUserByEmail(String email);

    boolean isUserExist(String userId);
    boolean isUserExistByEmail(String email);

    // --------------------------
    // Profile Management
    // --------------------------
    User updateUser(String userId, User updatedUser);   // Update editable fields (name, phone, about, email, profilePic)
    User uploadProfilePhoto(String userId, MultipartFile file); // Profile picture upload
    boolean changePassword(String userId, String currentPassword, String newPassword); // Secure password change

    // --------------------------
    // Delete
    // --------------------------
    void deleteUser(String id);
    void updateProfile(String userId, User formUser, MultipartFile profileImage);

    // User updateProfile(String userId, User formUser, MultipartFile profileImage);
}
