package com.society.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String relation;

    private Integer age;
}