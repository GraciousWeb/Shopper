package com.example.podb.Services;

import com.example.podb.DTO.ProductDTO;
import com.example.podb.DTO.ResponseData;
import com.example.podb.Model.Product;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDTO getProductById(Long id);
    ResponseData getAllProducts(Pageable pageable);
}
