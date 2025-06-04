package com.khata.product.controller;

import com.khata.payload.ApiResponse;
import com.khata.product.dto.CategoryDTO;
import com.khata.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product/category")
@RequiredArgsConstructor
@Hidden
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO category = categoryService.createCategory(categoryDTO);
        ApiResponse<CategoryDTO> response = new ApiResponse<>(
                category, HttpStatus.CREATED.value(), "Category Created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryDTO>>> getCategories(Pageable pageable) {
        Page<CategoryDTO> categoryDTOSPage = categoryService.getCategories(pageable);
        return ResponseEntity.ok(new ApiResponse<>(categoryDTOSPage, HttpStatus.OK.value()));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Integer categoryId) {
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryDTO, categoryId);
        ApiResponse<CategoryDTO> response = new ApiResponse<>(
                updatedCategory, HttpStatus.OK.value(), "Category Updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryDetails(@PathVariable Integer categoryId) {
        CategoryDTO categoryDTO = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(new ApiResponse<>(categoryDTO, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Integer categoryId) {
        categoryService.deleteCategory(categoryId);
        ApiResponse<Void> response = new ApiResponse<>(null, HttpStatus.OK.value(), "Category Deleted successfully");
        return ResponseEntity.ok(response);
    }
}
