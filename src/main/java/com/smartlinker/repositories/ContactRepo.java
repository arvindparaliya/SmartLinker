package com.smartlinker.repositories;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smartlinker.entities.Contact;
import com.smartlinker.entities.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {

    Page<Contact> findByUser(User user, Pageable pageable);
    List<Contact> findByUser(User user);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
    List<Contact> findByUserId(@Param("userId") String userId);

    Page<Contact> findByUserAndNameContaining(User user, String namekeyword, Pageable pageable);
    Page<Contact> findByUserAndEmailContaining(User user, String emailkeyword, Pageable pageable);
    Page<Contact> findByUserAndPhoneNumberContaining(User user, String phonekeyword, Pageable pageable);

    //  COUNT METHODS
    long countByUser(User user); 
    long countByUserAndFavoriteTrue(User user);
    long countByUserAndCreatedAtAfter(User user, LocalDateTime date); 

    List<Contact> findTop4ByUserOrderByCreatedAtDesc(User user);

    List<Contact> findByUserAndFavoriteTrue(User user);
    List<Contact> findByUserAndNameContainingIgnoreCase(User user, String name);

    long countByUser_IdAndCreatedAtBetween(String userId, LocalDateTime start, LocalDateTime end);


    long countByUserId(User user);
    


    long countRecentContactsByUser(User user);

    long countRecentContactsByUserAndCreatedAtAfter(User user, LocalDateTime timestamp);

    long countByUserAndLinkedInLinkIsNotNullAndLinkedInLinkNot(User user, String emptyString);

    long countByUserAndWebsiteLinkIsNotNullAndWebsiteLinkNot(User user, String emptyString);

    long countByUserAndEmailIsNullOrEmailEquals(User user, String emptyString);

    
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.user = :user AND (c.phoneNumber IS NULL OR c.address IS NULL OR c.address = :emptyString)")
    long countMissingPhoneOrAddress(@Param("user") User user, @Param("emptyString") String emptyString);


    Optional<Contact> findByIdAndUserId(String contactId, String userId);

    // Optional<Contact> findByIdAndUserId(Long contactId, Long userId);


    // List<Contact> findByUserAndFavoriteTrue(User user);
    
    List<Contact> findTop10ByUserOrderByCreatedAtDesc(User user);
    
    

    List<Contact> findByUserAndWebsiteLinkIsNotNull(User user);
    
    List<Contact> findByUserAndEmailIsNull(User user);
    
    List<Contact> findByUserAndAddressIsNull(User user);
    
    // List<Contact> findByUser(User user);

    List<Contact> findByUserAndLinkedInLinkIsNotNull(User user);

    long countByUserAndLinkedInLinkIsNotNull(User user);
    long countByUserAndWebsiteLinkIsNotNull(User user);
    long countByUserAndEmailIsNull(User user);
    long countByUserAndAddressIsNull(User user);
    

}
