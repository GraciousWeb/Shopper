package com.example.podb.Controller;

import com.example.podb.DTO.ProductDTO;
import com.example.podb.DTO.ResponseData;
import com.example.podb.Services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/product")
public class ProductController {
    private final ProductService productService;
    @GetMapping("/viewProduct/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id){
        ProductDTO foundProduct = productService.getProductById(id);
        if(foundProduct == null){
            throw new RuntimeException("No such post");
        }
        return new ResponseEntity<>(foundProduct, HttpStatus.OK);

    }
    @GetMapping("/getAllProducts")
    public ResponseEntity<ResponseData> getAllProducts (@RequestParam(defaultValue = "1") int pageNo,
                                                        @RequestParam (defaultValue = "5") int pageSize) throws RuntimeException{
        Pageable pageable = PageRequest.of(pageNo -1, pageSize);
        ResponseData responseData = productService.getAllProducts(pageable);
        return ResponseEntity.ok(responseData);
    }

}

