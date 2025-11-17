package com.society.management.service;

import com.society.management.dto.request.FamilyMemberRequest;
import com.society.management.dto.request.UserUpdateRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.UserDto;
import com.society.management.exception.ResourceNotFoundException;
import com.society.management.exception.UnauthorizedException;
import com.society.management.model.FamilyMember;
import com.society.management.model.User;
import com.society.management.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MemberService {

    @Autowired
    private UserRepository userRepository;

    // ======================== GET MEMBER PROFILE ========================
    public ApiResponse<UserDto> getMemberProfile(String memberId) {
        log.info("Fetching profile for member: {}", memberId);

        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        UserDto userDto = mapToUserDto(user);
        return ApiResponse.success(userDto, "Profile fetched successfully");
    }

    // ======================== UPDATE MEMBER PROFILE ========================
    public ApiResponse<UserDto> updateMemberProfile(String memberId, UserUpdateRequest request, String currentUserId) {
        log.info("Updating profile for member: {}", memberId);

        // Authorization check
        if (!memberId.equals(currentUserId)) {
            throw new UnauthorizedException("You can only update your own profile");
        }

        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        // Update fields
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getProfession() != null) user.setProfession(request.getProfession());

        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        log.info("Profile updated for member: {}", memberId);
        return ApiResponse.success(mapToUserDto(user), "Profile updated successfully");
    }

    // ======================== UPDATE PROFILE PHOTO ========================
    public ApiResponse<UserDto> updateProfilePhoto(String memberId, String photoUrl, String currentUserId) {
        log.info("Updating profile photo for: {}", memberId);

        if (!memberId.equals(currentUserId)) {
            throw new UnauthorizedException("You can only update your own profile");
        }

        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        user.setProfilePhotoUrl(photoUrl);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return ApiResponse.success(mapToUserDto(user), "Profile photo updated");
    }

    // ======================== GET ALL MEMBERS (ADMIN) ========================
    public ApiResponse<Page<UserDto>> getAllMembers(Pageable pageable) {
        log.info("Fetching all members");

        Page<User> members = userRepository.findAll(pageable);
        Page<UserDto> memberDtos = members.map(this::mapToUserDto);

        return ApiResponse.success(memberDtos, "Members fetched successfully");
    }

    // ======================== DELETE MEMBER (ADMIN) ========================
    public ApiResponse<Void> deleteMember(String memberId) {
        log.info("Deleting member: {}", memberId);

        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        userRepository.deleteById(memberId);

        log.info("Member deleted: {}", memberId);
        return ApiResponse.success(null, "Member deleted successfully");
    }

    // ======================== ADD FAMILY MEMBER ========================
    public ApiResponse<UserDto> addFamilyMember(String memberId, FamilyMemberRequest request, String currentUserId) {
        log.info("Adding family member for: {}", memberId);

        if (!memberId.equals(currentUserId)) {
            throw new UnauthorizedException("You can only add family members to your own profile");
        }

        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        FamilyMember familyMember = FamilyMember.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .relation(request.getRelation())
                .age(request.getAge())
                .build();

        user.getFamilyMembers().add(familyMember);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        log.info("Family member added for: {}", memberId);
        return ApiResponse.success(mapToUserDto(user), "Family member added successfully");
    }

    // ======================== UPDATE FAMILY MEMBER ========================
    public ApiResponse<UserDto> updateFamilyMember(String memberId, String familyMemberId,
                                                   FamilyMemberRequest request, String currentUserId) {
        log.info("Updating family member for: {}", memberId);

        if (!memberId.equals(currentUserId)) {
            throw new UnauthorizedException("You can only update your own family members");
        }

        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        FamilyMember familyMember = user.getFamilyMembers().stream()
                .filter(fm -> fm.getId().equals(familyMemberId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Family member not found"));

        familyMember.setName(request.getName());
        familyMember.setRelation(request.getRelation());
        familyMember.setAge(request.getAge());

        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return ApiResponse.success(mapToUserDto(user), "Family member updated successfully");
    }

    // ======================== DELETE FAMILY MEMBER ========================
    public ApiResponse<UserDto> deleteFamilyMember(String memberId, String familyMemberId, String currentUserId) {
        log.info("Deleting family member for: {}", memberId);

        if (!memberId.equals(currentUserId)) {
            throw new UnauthorizedException("You can only delete your own family members");
        }

        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        user.getFamilyMembers().removeIf(fm -> fm.getId().equals(familyMemberId));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return ApiResponse.success(mapToUserDto(user), "Family member deleted successfully");
    }

    // ======================== HELPER METHODS ========================
    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profession(user.getProfession())
                .societyCode(user.getSocietyCode())
                .houseNo(user.getHouseNo())
                .familyName(user.getFamilyName() != null ? user.getFamilyName() : user.getName())
                .totalMembers(user.getTotalMembers() != null ? user.getTotalMembers() :
                        (user.getFamilyMembers() != null ? user.getFamilyMembers().size() : 0))
                .professionDescription(user.getProfessionDescription())
                .profileImg(user.getProfilePhotoUrl())
                .publicUrl(user.getPublicUrl())
                .role(user.getRole())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .familyMembers(user.getFamilyMembers() != null ? user.getFamilyMembers() : Collections.emptyList())
                .advertises(user.getAdvertises() != null ? user.getAdvertises() : Collections.emptyList())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}