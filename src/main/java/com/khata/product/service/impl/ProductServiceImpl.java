package com.khata.product.service.impl;

import com.khata.auth.service.UserService;
import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.product.dto.ProductDTO;
import com.khata.product.dto.ProductDepartmentRateDTO;
import com.khata.product.entity.Product;
import com.khata.product.entity.ProductDepartmentRate;
import com.khata.product.repositories.ProductRepo;
import com.khata.product.service.ProductService;
import com.khata.settings.department.entity.Department;
import com.khata.settings.department.repositories.DepartmentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final DepartmentRepo departmentRepo;
    private final UserService userService;

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        validateProductCodeIsUnique(productDTO.getProductCode(), null, currentUserId);
        validateDuplicateDepartmentsInRequest(productDTO.getDepartmentRates());

        Product product = new Product();
        product.setCreatedUserId(currentUserId);
        setProductFields(product, productDTO);

        Product savedProduct = productRepo.save(product);
        log.info("Product created | id={} | code={}", savedProduct.getId(), savedProduct.getProductCode());
        return toProductDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO, Integer productId) {
        Integer currentUserId = userService.getCurrentUserId();
        Product product = getProductEntityById(productId, currentUserId);
        validateProductCodeIsUnique(productDTO.getProductCode(), productId, currentUserId);
        validateDuplicateDepartmentsInRequest(productDTO.getDepartmentRates());

        product.setProductCode(productDTO.getProductCode());
        product.setProductName(productDTO.getProductName());
        syncDepartmentRates(product, productDTO.getDepartmentRates());

        Product updatedProduct = productRepo.save(product);
        log.info("Product updated | id={}", productId);
        return toProductDTO(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Integer productId) {
        Integer currentUserId = userService.getCurrentUserId();
        Product product = getProductEntityById(productId, currentUserId);
        return toProductDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProducts(Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        Page<Product> products = productRepo.findByCreatedUserId(currentUserId, pageable);
        return products.map(this::toProductDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProductByName(String keyword, Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        Page<Product> products = productRepo.findByProductNameContainingIgnoreCaseAndCreatedUserId(keyword, currentUserId, pageable);
        return products.map(this::toProductDTO);
    }

    @Override
    @Transactional
    public void deleteProduct(Integer productId) {
        Integer currentUserId = userService.getCurrentUserId();
        Product product = getProductEntityById(productId, currentUserId);
        productRepo.delete(product);
        log.info("Product deleted | id={}", productId);
    }

    private void setProductFields(Product product, ProductDTO productDTO) {
        product.setProductCode(productDTO.getProductCode());
        product.setProductName(productDTO.getProductName());
        productDTO.getDepartmentRates().forEach(departmentRateDTO -> {
            ProductDepartmentRate departmentRate = buildDepartmentRate(product, departmentRateDTO);
            product.getDepartmentRates().add(departmentRate);
        });
    }

    private ProductDepartmentRate buildDepartmentRate(Product product, ProductDepartmentRateDTO departmentRateDTO) {
        Department department = getDepartmentEntityById(departmentRateDTO.getDepartmentId());
        ProductDepartmentRate departmentRate = new ProductDepartmentRate();
        departmentRate.setProduct(product);
        departmentRate.setDepartment(department);
        departmentRate.setRate(departmentRateDTO.getRate());
        return departmentRate;
    }

    private void syncDepartmentRates(Product product, List<ProductDepartmentRateDTO> departmentRateDTOs) {
        Set<Integer> requestedDepartmentIds = departmentRateDTOs.stream()
                .map(ProductDepartmentRateDTO::getDepartmentId)
                .collect(Collectors.toSet());

        Iterator<ProductDepartmentRate> iterator = product.getDepartmentRates().iterator();
        while (iterator.hasNext()) {
            ProductDepartmentRate existingRate = iterator.next();
            if (!requestedDepartmentIds.contains(existingRate.getDepartment().getId())) {
                iterator.remove();
            }
        }

        Map<Integer, ProductDepartmentRate> existingRatesByDepartmentId = product.getDepartmentRates().stream()
                .collect(Collectors.toMap(rate -> rate.getDepartment().getId(), Function.identity()));

        for (ProductDepartmentRateDTO departmentRateDTO : departmentRateDTOs) {
            ProductDepartmentRate existingRate = existingRatesByDepartmentId.get(departmentRateDTO.getDepartmentId());
            if (existingRate != null) {
                existingRate.setRate(departmentRateDTO.getRate());
            } else {
                ProductDepartmentRate newRate = buildDepartmentRate(product, departmentRateDTO);
                product.getDepartmentRates().add(newRate);
            }
        }
    }

    private Product getProductEntityById(Integer productId, Integer currentUserId) {
        return productRepo.findByIdAndCreatedUserId(productId, currentUserId).orElseThrow(
                () -> new ResourceNotFoundException("Product", "id", productId)
        );
    }

    private Department getDepartmentEntityById(Integer departmentId) {
        return departmentRepo.findById(departmentId).orElseThrow(
                () -> new ResourceNotFoundException("Department", "id", departmentId)
        );
    }

    private void validateProductCodeIsUnique(String productCode, Integer productId, Integer currentUserId) {
        Optional<Product> existingProduct = productRepo.findByProductCodeAndCreatedUserId(productCode, currentUserId);
        if (existingProduct.isPresent() && !existingProduct.get().getId().equals(productId)) {
            throw new ResourceAlreadyExistsException("Product code", productCode);
        }
    }

    private void validateDuplicateDepartmentsInRequest(List<ProductDepartmentRateDTO> departmentRates) {
        Set<Integer> departmentIds = new HashSet<>();
        for (ProductDepartmentRateDTO departmentRate : departmentRates) {
            if (!departmentIds.add(departmentRate.getDepartmentId())) {
                throw new ResourceAlreadyExistsException("Product department rate", departmentRate.getDepartmentId());
            }
        }
    }

    private ProductDTO toProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setProductCode(product.getProductCode());
        productDTO.setProductName(product.getProductName());
        productDTO.setCreatedUserId(product.getCreatedUserId());
        productDTO.setDepartmentRates(product.getDepartmentRates().stream()
                .map(this::toProductDepartmentRateDTO)
                .toList());
        return productDTO;
    }

    private ProductDepartmentRateDTO toProductDepartmentRateDTO(ProductDepartmentRate departmentRate) {
        ProductDepartmentRateDTO departmentRateDTO = new ProductDepartmentRateDTO();
        departmentRateDTO.setId(departmentRate.getId());
        departmentRateDTO.setDepartmentId(departmentRate.getDepartment().getId());
        departmentRateDTO.setDepartmentName(departmentRate.getDepartment().getDepartmentName());
        departmentRateDTO.setRate(departmentRate.getRate());
        return departmentRateDTO;
    }
}
