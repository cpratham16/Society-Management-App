package com.society.management.service;

import com.society.management.dto.request.ContactRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.ContactDto;
import com.society.management.exception.ResourceNotFoundException;
import com.society.management.model.Contact;
import com.society.management.model.User;
import com.society.management.repository.ContactRepository;
import com.society.management.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // SUBMIT CONTACT FORM
    public ApiResponse<Void> submitContactForm(ContactRequest request) {
        log.info("Submitting contact form from: {}", request.getEmail());

        Contact contact = Contact.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .message(request.getMessage())
                .createdAt(Instant.now())
                .build();

        contactRepository.save(contact);

        // Send notification to admin
        User admin = userRepository.findByEmail("admin@society.com").orElse(null);
        if (admin != null) {
            emailService.sendContactFormNotificationEmail(admin.getEmail(), request.getName(), request.getEmail());
        }

        return ApiResponse.success(null, "Thank you for contacting us. We will get back to you soon.");
    }

    // GET ALL CONTACTS (ADMIN)
    public ApiResponse<Page<ContactDto>> getAllContacts(Pageable pageable) {
        log.info("Fetching all contacts");

        Page<Contact> contacts = contactRepository.findAllByOrderByCreatedAtDesc(pageable);
        Page<ContactDto> contactDtos = contacts.map(this::mapToDto);

        return ApiResponse.success(contactDtos, "Contacts fetched successfully");
    }

    // DELETE CONTACT (ADMIN)
    public ApiResponse<Void> deleteContact(String contactId) {
        log.info("Deleting contact: {}", contactId);

        if (!contactRepository.existsById(contactId)) {
            throw new ResourceNotFoundException("Contact not found");
        }

        contactRepository.deleteById(contactId);
        return ApiResponse.success(null, "Contact deleted successfully");
    }

    private ContactDto mapToDto(Contact contact) {
        return ContactDto.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .message(contact.getMessage())
                .createdAt(contact.getCreatedAt())
                .build();
    }
}