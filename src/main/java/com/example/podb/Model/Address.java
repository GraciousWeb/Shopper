package com.example.podb.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name ="address")

public class Address {
    @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        private Long id;

        @Column(name = "address_line_1", nullable = false, length = 512)
        private String adressLine1;

        @Column(name = "adress_line_2", length = 512)
        private String adressLine2;

        @Column(name = "city", nullable = false)
        private String city;

        @Column(name = "country", nullable = false, length = 75)
        private String country;

        @ManyToOne(optional = false)
        @JoinColumn(name = "user_id", nullable = false)
        private LocalUser user;

        public LocalUser getUser() {
            return user;
        }

        public void setUser(LocalUser user) {
            this.user = user;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }


}
