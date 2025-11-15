package com.society.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMember {
    private String id; // Unique ID within the user's family list
    private String name;
    private String relation; // Parent, Child, Spouse, etc.
    private Integer age;
    private String photoUrl; // From Cloudinary
}