package com.example.podb.token;

import com.example.podb.Model.LocalUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor

public class PasswordToken {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    @Column(nullable = false)
    private  String verificationToken;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isValid = false;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "local_user")
    private LocalUser user;

    public PasswordToken(String verificationToken, LocalDateTime createdAt, LocalDateTime expiresAt, LocalUser user) {
        this.verificationToken = UUID.randomUUID().toString();
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.user = user;
    }


}
