package com.example.podb.Model;

import com.example.podb.Enums.Roles;
import com.example.podb.token.PasswordToken;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "local_user")
public class LocalUser implements UserDetails {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        private Long id;


        @Column(name = "username", nullable = false, unique = true)
        private String username;

        @Column(name = "password", nullable = false, length = 1000)
            private String password;

        @Column(name = "email", nullable = false, unique = true, length = 320)
        private String email;

        @Column(name = "firstName", nullable = false)
        private String firstName;

        @Column(name = "lastName", nullable = false)
        private String lastName;
        @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        private PasswordToken passwordToken;
        @Enumerated(EnumType.STRING)
        private Roles role;
        private Boolean locked = true;
        private Boolean enabled = false;
        private Boolean valid = false;
        private Boolean isVerified = false;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority(role.name()));
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
