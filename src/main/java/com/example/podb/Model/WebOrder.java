package com.example.podb.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class WebOrder {

      @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        private Long id;

        @ManyToOne(optional = false)
        @JoinColumn(name = "user_id", nullable = false)
        private LocalUser user;

        @ManyToOne(optional = false)
        @JoinColumn(name = "address_id", nullable = false)
        private Address address;

        @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
        private List<WebOrderQuantities> quantities = new ArrayList<>();
        
}
