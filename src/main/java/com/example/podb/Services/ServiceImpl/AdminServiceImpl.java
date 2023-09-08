package com.example.podb.Services.ServiceImpl;
import com.example.podb.DTO.ProductDTO;
import com.example.podb.Model.Admin;
import com.example.podb.Model.Product;
import com.example.podb.Repository.AdminRepository;
import com.example.podb.Repository.ProductRepository;
import com.example.podb.Services.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final ProductRepository productRepository;


    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);

    }

//    @Override
//    public Admin saveAdmin(Admin adminDTO) {
//        Admin admin = new Admin();
//        admin.setFirstName(adminDTO.getFirstName());
//        admin.setLastName(adminDTO.getLastName());
//        admin.setPassword(adminDTO.getPassword());
//        admin.setUsername(adminDTO.getUsername());
//
////        Set<Role> roles = new HashSet<>();
////        roles.add(roleRepository.findByName("ADMIN"));
////        admin.setRoles(roles);
//        return adminRepository.save(admin);
//    }
        @Override
        public ProductDTO addProduct(ProductDTO productDTO) {
            Product product = map2Product(productDTO);
            Product savedProduct = productRepository.save(product);
            return map2ProductDTO(savedProduct);

        }

        @Override
        public ProductDTO updateProduct(ProductDTO productDTO, Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setShortDescription(productDTO.getShortDescription());
        product.setLongDescription(productDTO.getLongDescription());
        product.setInventory(productDTO.getInventory());
        product.setName(productDTO.getName());
       Product updatedProduct = productRepository.save(product);

         return map2ProductDTO(updatedProduct);
        }

        @Override
        public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        }




        Product map2Product(ProductDTO productDTO){
            Product product = new Product();
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setInventory(productDTO.getInventory());
            product.setShortDescription(productDTO.getShortDescription());
            product.setLongDescription(productDTO.getLongDescription());
            return product;
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
