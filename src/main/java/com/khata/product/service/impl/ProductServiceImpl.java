package com.khata.product.service.impl;

import com.khata.exceptions.ResourceNotFoundException;
import com.khata.product.dto.ProductDTO;
import com.khata.product.entity.Category;
import com.khata.product.entity.Product;
import com.khata.product.repositories.CategoryRepo;
import com.khata.product.repositories.ProductRepo;
import com.khata.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ModelMapper modelMapper;
    private final CategoryRepo categoryRepo;

    public ProductServiceImpl(ProductRepo productRepo, ModelMapper modelMapper, CategoryRepo categoryRepo) {
        this.productRepo = productRepo;
        this.modelMapper = modelMapper;
        this.categoryRepo = categoryRepo;

        configureModelMapperForProductToProductDTO();
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        String productId = generateProductId(productDTO.getName());
        product.setProductId(productId);
        Category category = getCategoryEntityById(productDTO.getCategory());
        product.setCategory(category);
        Product savedProduct = productRepo.save(product);
        log.info("Product created with title: {}", product.getName());
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO, Integer productId) {
        Product product = getProductEntityById(productId);
        product.setName(productDTO.getName());
        product.setQuantity(productDTO.getQuantity());
        product.setSellingPrice(productDTO.getSellingPrice());
        product.setPurchasePrice(productDTO.getPurchasePrice());

        Category category = getCategoryEntityById(productDTO.getCategory());
        product.setCategory(category);

        Product updatedProduct = productRepo.save(product);
        log.info("Product updated with ID: {}", productId);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Integer productId) {
        Product product = getProductEntityById(productId);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProducts(Pageable pageable) {
        Page<Product> products = productRepo.findAll(pageable);
        return products.map(product -> modelMapper.map(product, ProductDTO.class));
    }

    @Override
    public void deleteProduct(Integer productId) {
        Product product = getProductEntityById(productId);
        productRepo.delete(product);
        log.info("Product deleted with ID: {}", productId);
    }


    private Product getProductEntityById(Integer productId) {
        return productRepo.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product", "id", productId)
        );
    }

    private Category getCategoryEntityById(Integer categoryId) {
        return categoryRepo.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category", "id", categoryId)
        );
    }

    private void configureModelMapperForProductToProductDTO() {
        if (this.modelMapper.getTypeMap(Product.class, ProductDTO.class) == null) {
            this.modelMapper.typeMap(Product.class, ProductDTO.class)
                    .addMapping(src -> src.getCategory().getId(), ProductDTO::setCategory);
        }
    }

    /**
     * Generates a unique product ID using the first two letters of the product name
     * and a random 8-character UUID.
     *
     * @param productName The name of the product.
     * @return A unique product ID in the format "<prefix>_<UUID>".
     */
    private String generateProductId(String productName) {
        String prefix = productName.substring(0, 2).toUpperCase();
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "_" + uniqueSuffix;
    }

}
