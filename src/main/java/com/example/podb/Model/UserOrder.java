package com.example.podb.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "user_order")
public class UserOrder {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        private Long id;

        @ManyToOne(optional = false)
        @JoinColumn(name = "user_id", nullable = false)
        private LocalUser user;
        
        @ManyToOne(optional = false)
        @JoinColumn(name = "address", nullable = false)
        private Address address;
        
}
