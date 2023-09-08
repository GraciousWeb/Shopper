package com.example.podb.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SignUpDto {
    private String username;

    private String password;

    private String email;

    private String firstName;

    private String lastName;
}
