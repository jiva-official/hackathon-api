package com.codesurge.hackathon.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {
   
    @Size(min = 3, message = "Team name must be at least 3 characters")
    private String teamName;
}