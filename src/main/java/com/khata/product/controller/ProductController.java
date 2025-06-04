package com.khata.product.controller;

import com.khata.payload.ApiResponse;
import com.khata.product.dto.ProductDTO;
import com.khata.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO productDTO){
        ProductDTO product = productService.createProduct(productDTO);
        ApiResponse<ProductDTO> response = new ApiResponse<>(product, HttpStatus.CREATED.value(), "Product Created Successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getProducts(Pageable pageable){
        Page<ProductDTO> product = productService.getProducts(pageable);
        return ResponseEntity.ok(new ApiResponse<>(product, HttpStatus.OK.value()));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @Valid @RequestBody ProductDTO productDTO,
            @PathVariable Integer productId){
        ProductDTO updatedProduct = productService.updateProduct(productDTO, productId);
        ApiResponse<ProductDTO> response = new ApiResponse<>(updatedProduct, HttpStatus.OK.value(), "Product Updated Successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductDetails(@PathVariable Integer productId){
        ProductDTO productDTO = productService.getProductById(productId);
        return ResponseEntity.ok(new ApiResponse<>(productDTO, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer productId){
        productService.deleteProduct(productId);
        return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value() ,"Product Deleted Successfully"));
    }
}
