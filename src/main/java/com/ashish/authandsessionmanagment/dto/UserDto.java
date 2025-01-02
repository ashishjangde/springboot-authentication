package com.ashish.authandsessionmanagment.dto;

import com.ashish.authandsessionmanagment.entities.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String id;

    private String name;

    private String email;

    private Boolean isVerified;

    private Set<Roles> roles;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
