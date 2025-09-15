package com.smartlinker.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.smartlinker.entities.User;
import com.smartlinker.helpers.AppConstants;
import com.smartlinker.exception.EmailAlreadyExistsException;
import com.smartlinker.helpers.Helper;
import com.smartlinker.exception.ResourceNotFoundException;
import com.smartlinker.repositories.UserRepo;
import com.smartlinker.services.EmailService;
import com.smartlinker.services.UserService;

import java.io.IOException;
import java.nio.file.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Helper helper;

    // private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String UPLOAD_DIR = "uploads/profile_photos/";

    // Registration
    @Override
    public User saveUser(User user) {
        try {
            String userId = UUID.randomUUID().toString();
            user.setId(userId);

            String rawPassword = user.getPassword();
            System.out.println("Raw Password entered by user: " + rawPassword);

            String encodedPassword = passwordEncoder.encode(rawPassword);
            System.out.println("Encoded Password (bcrypt): " + encodedPassword);

            user.setPassword(encodedPassword);

            user.setRoleList(Set.of(AppConstants.ROLE_USER));

            String emailToken = UUID.randomUUID().toString();
            user.setEmailToken(emailToken);

            User savedUser = userRepo.save(user);

            // send verification email
            String emailLink = helper.getLinkForEmailVerificatiton(emailToken);
            emailService.sendEmail(savedUser.getEmail(),
                    "Verify Account : Smart Contact Manager", emailLink);

            return savedUser;

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")
                    && e.getMessage().contains("users.UK_6dotkott2kjsp8vw4d0m25fb7")) {
                throw new EmailAlreadyExistsException("This email is already registered.");
            }
            throw e;
        }
    }

    // User retrieval
    @Override
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    @Override
    public boolean isUserExist(String userId) {
        return userRepo.findById(userId).isPresent();
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    // Profile update 
    @Override
    public User updateUser(String userId, User updatedUser) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setAbout(updatedUser.getAbout());
        user.setProfilePic(updatedUser.getProfilePic());

        return userRepo.save(user);
    }


    // Profile photo upload
    @Override
    public User uploadProfilePhoto(String userId, MultipartFile file) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!file.isEmpty()) {
            try {
                Files.createDirectories(Paths.get(UPLOAD_DIR));
                String fileName = userId + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, file.getBytes());

                user.setProfilePic("/" + UPLOAD_DIR + fileName);
                return userRepo.save(user);

            } catch (IOException e) {
                throw new RuntimeException("Failed to upload profile photo", e);
            }
        }
        return user;
    }

    // Change passeord
    @Override
    public boolean changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false; 
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        return true;
    }

    // Delete user
    @Override
    public void deleteUser(String id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepo.delete(user);
    }

    // Registration 
    @Override
    public User registerUser(User user) {
        Optional<User> existing = userRepo.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new EmailAlreadyExistsException("This email is already registered.");
        }
        return saveUser(user);
    }

    @Override
    public void updateProfile(String userId, User formUser, MultipartFile profileImage) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
  
        user.setName(formUser.getName());
        user.setEmail(formUser.getEmail());
        user.setPhoneNumber(formUser.getPhoneNumber());
        user.setAbout(formUser.getAbout());

        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String filename = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
                Path uploadPath = Paths.get("uploads/profile_photos");
                Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(filename);
                Files.copy(profileImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                user.setProfilePic("/uploads/profile_photos/" + filename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload profile image", e);
            }
        }

        userRepo.save(user);
    }

}
