package com.khata.product.service.impl;

import com.khata.exceptions.ResourceNotFoundException;
import com.khata.product.dto.CategoryDTO;
import com.khata.product.entity.Category;
import com.khata.product.repositories.CategoryRepo;
import com.khata.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepo categoryRepo, ModelMapper modelMapper) {
        this.categoryRepo = categoryRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory = categoryRepo.save(category);
        log.info("Category created with title: {}", category.getTitle());
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Integer categoryId) {
        Category category = getCategoryEntityById(categoryId);
        category.setTitle(categoryDTO.getTitle());
        Category updatedCategory = categoryRepo.save(category);
        log.info("Category updated with ID: {}", categoryId);
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Integer categoryId) {
        Category category = getCategoryEntityById(categoryId);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> getCategories(Pageable pageable) {
        Page<Category> categories = categoryRepo.findAll(pageable);
        return categories.map(category -> modelMapper.map(category, CategoryDTO.class));
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        Category category = getCategoryEntityById(categoryId);
        categoryRepo.delete(category);
        log.info("Category deleted with ID: {}", categoryId);
    }

    private Category getCategoryEntityById(Integer categoryId) {
        return categoryRepo.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category", "id", categoryId)
        );
    }
}
