package com.example.podb.Controller;


import com.example.podb.DTO.ProductDTO;
import com.example.podb.DTO.SignUpDto;
import com.example.podb.Services.AdminService;
import com.example.podb.Services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admins")
public class AdminController {
    private final AdminService adminService;
    private final UserServices userServices;


    @PostMapping("createAdmin")
    public ResponseEntity<SignUpDto> createAdmin(@RequestBody SignUpDto signUpDto) {
        SignUpDto registeredUser = userServices.createAdmin(signUpDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);

    }
    @PostMapping("addProduct")
    public ResponseEntity<ProductDTO> addProduct (@RequestBody ProductDTO productDTO){
        ProductDTO savedProduct = adminService.addProduct(productDTO);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PutMapping("updateProduct/{id}")
    public ResponseEntity<ProductDTO> updateProduct (@RequestBody ProductDTO productDTO, @PathVariable Long id){
        ProductDTO updatedProduct = adminService.updateProduct(productDTO, id);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
    @DeleteMapping("deleteProduct/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        adminService.deleteProduct(id);
        return new ResponseEntity<>("Product deleted!", HttpStatus.OK);
    }


}
