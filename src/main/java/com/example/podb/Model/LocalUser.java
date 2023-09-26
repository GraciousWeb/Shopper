package com.example.podb.Model;

import com.example.podb.Enums.Roles;
import com.example.podb.token.ConfirmationToken;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
public class LocalUser implements UserDetails {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;


        @Column(nullable = false, length = 1000)
            private String password;

        @Column(nullable = false, unique = true, length = 320)
        private String email;

        @Column(nullable = false)
        private String firstName;

        @Column(nullable = false)
        private String lastName;
        @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        private ConfirmationToken confirmationToken;
        @Enumerated(EnumType.STRING)
        private Roles role;
        private Boolean locked = true;
        private Boolean enabled = false;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority(role.name()));
        }

        @Override
        public String getUsername() {
                return email;
        }

        @Override
        public boolean isAccountNonExpired() {
                return true;
        }

        @Override
        public boolean isAccountNonLocked() {
                return locked;
        }

        @Override
        public boolean isCredentialsNonExpired() {
                return true;
        }

        @Override
        public boolean isEnabled() {
                return enabled;
        }
}
