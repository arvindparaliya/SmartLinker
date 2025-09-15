package com.smartlinker.services.impl;

import java.time.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.smartlinker.entities.Contact;
import com.smartlinker.entities.User;
import com.smartlinker.exception.ResourceNotFoundException;
import com.smartlinker.repositories.ContactRepo;
import com.smartlinker.services.ContactService;

import jakarta.transaction.Transactional;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Override
    public Contact save(Contact contact) {
        contact.setId(UUID.randomUUID().toString());
        return contactRepo.save(contact);
    }

    @Transactional
    @Override
    public Contact update(Contact contact) {
        Contact contactOld = contactRepo.findById(contact.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        
        contactOld.setName(contact.getName());
        contactOld.setEmail(contact.getEmail());
        contactOld.setPhoneNumber(contact.getPhoneNumber());
        contactOld.setAddress(contact.getAddress());
        contactOld.setDescription(contact.getDescription());
        contactOld.setPicture(contact.getPicture());
        contactOld.setFavorite(contact.isFavorite());
        contactOld.setWebsiteLink(contact.getWebsiteLink());
        contactOld.setLinkedInLink(contact.getLinkedInLink());
        contactOld.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());

        return contactRepo.saveAndFlush(contactOld);
    }

    @Override
    public List<Contact> getAll() {
        return contactRepo.findAll();
    }

    @Override
    public Contact getById(String id) {
        return contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
    }

    @Transactional
    @Override
    public void delete(String id) {
        Contact contact = contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
        contactRepo.delete(contact);
    }

    @Override
    public List<Contact> getByUserId(String userId) {
        return contactRepo.findByUserId(userId);
    }

    @Override
    public Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUser(user, pageable);
    }

    @Override
    public Page<Contact> searchByName(String nameKeyword, int size, int page, String sortBy, String order, User user) {
        Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndNameContaining(user, nameKeyword, pageable);
    }

    @Override
    public Page<Contact> searchByEmail(String emailKeyword, int size, int page, String sortBy, String order, User user) {
        Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndEmailContaining(user, emailKeyword, pageable);
    }

    @Override
    public Page<Contact> searchByPhoneNumber(String phoneKeyword, int size, int page, String sortBy, String order, User user) {
        Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndPhoneNumberContaining(user, phoneKeyword, pageable);
    }

    // Count methods implementation
    @Override
    public long countFavoritesByUser(User user) {
        return contactRepo.countByUserAndFavoriteTrue(user);
    }

    @Override
    public long countRecentByUser(User user) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return contactRepo.countByUserAndCreatedAtAfter(user, oneMonthAgo);
    }

    @Override
    public long countWithLinkedInByUser(User user) {
        return contactRepo.countByUserAndLinkedInLinkIsNotNull(user);
    }

    @Override
    public long countWithWebsiteByUser(User user) {
        return contactRepo.countByUserAndWebsiteLinkIsNotNull(user);
    }

    @Override
    public long countMissingEmailByUser(User user) {
        return contactRepo.countByUserAndEmailIsNull(user);
    }

    @Override
    public long countMissingAddressByUser(User user) {
        return contactRepo.countByUserAndAddressIsNull(user);
    }

    // Filter methods implementation
    @Override
    public List<Contact> findFavoritesByUser(User user) {
        return contactRepo.findByUserAndFavoriteTrue(user);
    }

    @Override
    public List<Contact> findRecentByUser(User user) {
        return contactRepo.findTop10ByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public List<Contact> findWithLinkedInByUser(User user) {
        return contactRepo.findByUserAndLinkedInLinkIsNotNull(user);
    }

    @Override
    public List<Contact> findWithWebsiteByUser(User user) {
        return contactRepo.findByUserAndWebsiteLinkIsNotNull(user);
    }

    @Override
    public List<Contact> findMissingEmailByUser(User user) {
        return contactRepo.findByUserAndEmailIsNull(user);
    }

    @Override
    public List<Contact> findMissingAddressByUser(User user) {
        return contactRepo.findByUserAndAddressIsNull(user);
    }

    @Override
    public List<Contact> findAllByUser(User user) {
        return contactRepo.findByUser(user);
    }

    // Additional methods
    @Override
    public long getNewContactsThisWeek(String userId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        return contactRepo.countByUser_IdAndCreatedAtBetween(
            userId,
            startOfWeek.atStartOfDay(),
            endOfWeek.atTime(LocalTime.MAX)
        );
    }

    @Override
    public List<Contact> getFavoriteContacts(User user) {
        return contactRepo.findByUserAndFavoriteTrue(user);
    }

    @Override
    public long getTotalContactsByUser(User user) {
        return contactRepo.countByUser(user);
    }

    @Override
    public List<Contact> getContactsByUser(User user) {
        return contactRepo.findByUser(user);
    }

    @Override
    public Optional<Contact> getContactByIdAndUser(String contactId, String userId) {
        return contactRepo.findByIdAndUserId(contactId, userId);
    }

    
}