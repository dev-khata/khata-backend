package com.khata.product.service;

import com.khata.product.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(ProductDTO productDTO, Integer productId);
    ProductDTO getProductById(Integer productId);
    Page<ProductDTO> getProducts(Pageable pageable);
    Page<ProductDTO> searchProductByName(String keyword, Pageable pageable);
    void deleteProduct(Integer productId);
}
