package com.society.management.dto.response;

import com.society.management.model.FamilyMember;
import com.society.management.model.ProfileAdvertise;
import com.society.management.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String profession;
    private String societyCode;
    private String houseNo;
    private String familyName;
    private Integer totalMembers;
    private String professionDescription;
    private String profileImg;
    private String publicUrl;
    private Role role;
    private String profilePhotoUrl;
    private List<FamilyMember> familyMembers;
    private List<ProfileAdvertise> advertises;
    private Instant createdAt;
    private Instant updatedAt;
}