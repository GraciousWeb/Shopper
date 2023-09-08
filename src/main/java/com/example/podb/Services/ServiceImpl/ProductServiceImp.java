package com.example.podb.Services.ServiceImpl;

import com.example.podb.DTO.ProductDTO;
import com.example.podb.DTO.ResponseData;
import com.example.podb.Model.Product;
import com.example.podb.Repository.ProductRepository;
import com.example.podb.Services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;


    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return map2ProductDTO(product);

    }
    @Override
    public ResponseData getAllProducts(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);
        List<ProductDTO> productDTOList = convertToDTOList(page.getContent());
        ResponseData responseData = new ResponseData();
        responseData.setResponse(productDTOList);
        responseData.setFirst(page.isFirst());
        responseData.setLast(page.isLast());
        responseData.setPageNo(page.getNumber());
        responseData.setPageSize(page.getSize());
        responseData.setTotalItems(page.getNumberOfElements());
        log.info("Response Data {} ",responseData);

        return responseData;
    }
    List<ProductDTO>convertToDTOList(List<Product>productList){
        return productList.stream().map(this::map2ProductDTO).collect(Collectors.toList());
    }
    ProductDTO map2ProductDTO(Product product){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setInventory(product.getInventory());
        productDTO.setShortDescription(product.getShortDescription());
        productDTO.setLongDescription(product.getLongDescription());
        return productDTO;

    }
}

