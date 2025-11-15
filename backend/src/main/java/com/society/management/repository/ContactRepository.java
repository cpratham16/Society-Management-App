package com.society.management.repository;

import com.society.management.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends MongoRepository<Contact, String> {
    Page<Contact> findAllByOrderByCreatedAtDesc(Pageable pageable);
}