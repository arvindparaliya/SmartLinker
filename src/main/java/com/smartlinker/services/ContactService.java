package com.smartlinker.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.smartlinker.entities.Contact;
import com.smartlinker.entities.User;

public interface ContactService {

    Contact save(Contact contact);
    Contact update(Contact contact);
    List<Contact> getAll();
    Contact getById(String id);
    void delete(String id);
    List<Contact> getByUserId(String userId);
    Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction);
    Page<Contact> searchByName(String nameKeyword, int size, int page, String sortBy, String order, User user);
    Page<Contact> searchByEmail(String emailKeyword, int size, int page, String sortBy, String order, User user);
    Page<Contact> searchByPhoneNumber(String phoneKeyword, int size, int page, String sortBy, String order, User user);

    List<Contact> getFavoriteContacts(User user);

    //total contacts by userId
    long getTotalContactsByUser(User user);

    long getNewContactsThisWeek(String userId);

    List<Contact> getContactsByUser(User user);


    Optional<Contact> getContactByIdAndUser(String contactId, String userId);
    
    public List<Contact> findFavoritesByUser(User user);
    
    public List<Contact> findRecentByUser(User user) ;
    
    public List<Contact> findWithLinkedInByUser(User user) ;

    public List<Contact> findWithWebsiteByUser(User user);
    
    public List<Contact> findMissingEmailByUser(User user) ;

    public List<Contact> findMissingAddressByUser(User user);

    public List<Contact> findAllByUser(User user);

    
//
    
    public long countFavoritesByUser(User user);
    public long countRecentByUser(User user) ;
    public long countWithLinkedInByUser(User user); 
    public long countWithWebsiteByUser(User user);
    public long countMissingEmailByUser(User user) ;
    public long countMissingAddressByUser(User user) ;

    
}

