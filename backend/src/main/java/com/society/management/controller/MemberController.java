package com.society.management.controller;

import com.society.management.dto.request.FamilyMemberRequest;
import com.society.management.dto.request.UserUpdateRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.UserDto;
import com.society.management.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000"})
@Slf4j
public class MemberController {

    @Autowired
    private MemberService memberService;

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (String) auth.getPrincipal();
    }

    @GetMapping("/{memberId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> getMemberProfile(@PathVariable String memberId) {
        log.info("Get member profile for: {}", memberId);
        return ResponseEntity.ok(memberService.getMemberProfile(memberId));
    }

    @PutMapping("/{memberId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(@PathVariable String memberId,
                                                              @Valid @RequestBody UserUpdateRequest request) {
        log.info("Update profile for: {}", memberId);
        String currentUserId = getCurrentUserId();
        return ResponseEntity.ok(memberService.updateMemberProfile(memberId, request, currentUserId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getAllMembers(Pageable pageable) {
        log.info("Get all members - Admin");
        return ResponseEntity.ok(memberService.getAllMembers(pageable));
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable String memberId) {
        log.info("Delete member: {}", memberId);
        return ResponseEntity.ok(memberService.deleteMember(memberId));
    }

    @PostMapping("/{memberId}/family")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> addFamilyMember(@PathVariable String memberId,
                                                                @Valid @RequestBody FamilyMemberRequest request) {
        log.info("Add family member for: {}", memberId);
        String currentUserId = getCurrentUserId();
        return ResponseEntity.ok(memberService.addFamilyMember(memberId, request, currentUserId));
    }

    @PutMapping("/{memberId}/family/{familyMemberId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateFamilyMember(@PathVariable String memberId,
                                                                   @PathVariable String familyMemberId,
                                                                   @Valid @RequestBody FamilyMemberRequest request) {
        log.info("Update family member for: {}", memberId);
        String currentUserId = getCurrentUserId();
        return ResponseEntity.ok(memberService.updateFamilyMember(memberId, familyMemberId, request, currentUserId));
    }

    @DeleteMapping("/{memberId}/family/{familyMemberId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> deleteFamilyMember(@PathVariable String memberId,
                                                                   @PathVariable String familyMemberId) {
        log.info("Delete family member from: {}", memberId);
        String currentUserId = getCurrentUserId();
        return ResponseEntity.ok(memberService.deleteFamilyMember(memberId, familyMemberId, currentUserId));
    }
}