package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.CategoryDTO;
import com.arius.ecommerce.dto.response.BasePagination;
import com.arius.ecommerce.entity.Category;

public interface CategoryService {

    CategoryDTO createCategory(Category category);

    BasePagination<CategoryDTO> getAllCategories(int pageNumber, int pageSize, String sortBy, String sortDir);

    CategoryDTO getCategoryById(Long categoryId);

    CategoryDTO updateCategory(Category category, Long categoryId);

    CategoryDTO deleteCategory(Long categoryId);
}
