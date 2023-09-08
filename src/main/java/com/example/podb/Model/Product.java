package com.example.podb.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "product")

public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false )
        private Long id;
    
        @Column(name = "name", nullable = false, unique = true)
        private String name;
    
        @Column(name = "short_description", nullable = false)
        private String shortDescription;
    
        @Column(name = "Long_description")
        private String longDescription;
    
        @Column(name = "price", nullable = false)
        private Double price;
    
        public Double getPrice() {
            return price;
        }
    
        public void setPrice(Double price) {
    
            this.price = price;
        }
    
        @OneToOne(mappedBy = "product", cascade = CascadeType.REMOVE)
        private Inventory inventory;
    
        public Inventory getInventory() {
            return inventory;
        }
    
        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }
    
        public String getLongDescription() {
    
            return longDescription;
        }
    
        public void setLongDescription(String longDescription) {
    
            this.longDescription = longDescription;
        }
    
        public String getShortDescription() {
    
            return shortDescription;
        }
    
        public void setShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
    
        }
    
        public String getName() {
    
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    
        public Long getId() {
    
            return id;
        }
    
        public void setId(Long id) {
            this.id = id;
        }
}
