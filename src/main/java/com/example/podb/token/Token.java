package com.example.podb.token;

import com.example.podb.Model.LocalUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@Builder
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String token;
    private boolean isRevoked;
    private boolean isExpired;
    @ManyToOne
    private LocalUser users;
}
