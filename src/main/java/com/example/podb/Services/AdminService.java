package com.example.podb.Services;


import com.example.podb.DTO.ProductDTO;
import com.example.podb.Model.Admin;

public interface AdminService {

    Admin findByUsername(String username);

    ProductDTO addProduct(ProductDTO productDTO);

    ProductDTO updateProduct(ProductDTO productDTO, Long id);

    void deleteProduct(Long id);


}
