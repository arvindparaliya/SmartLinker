package com.smartlinker.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartlinker.entities.User;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndPassword(String email, String password);

    Optional<User> findByEmailToken(String id);

    User getUserByName(String name);

    //  Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    User getById(String userId);
    

}