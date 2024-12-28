package com.elite_gear_backend.dto;

import com.elite_gear_backend.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String token;
    private Role type;
}