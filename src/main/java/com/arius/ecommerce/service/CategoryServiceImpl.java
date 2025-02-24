package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.CategoryDTO;
import com.arius.ecommerce.dto.response.CategoryResponse;
import com.arius.ecommerce.entity.Category;
import com.arius.ecommerce.entity.product.Product;
import com.arius.ecommerce.exception.APIException;
import com.arius.ecommerce.exception.ResourceNotFoundException;
import com.arius.ecommerce.repository.CategoryRepository;
import com.arius.ecommerce.utils.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDTO createCategory(Category category) {
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());

        if (savedCategory != null){
            throw new APIException("Category "+ category.getCategoryName()+" already exists");
        }

        savedCategory = categoryRepository.save(category);

        return CommonMapper.INSTANCE.toCategoryDTO(savedCategory);
    }

    @Override
    public CategoryResponse getAllCategories(int pageNumber, int pageSize, String sortBy, String sortDir) {
        //Sorting
        Sort sort = sortBy.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        //Pagination
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sort);
        Page<Category> pagedCategories = categoryRepository.findAll(pageDetails);

        List<Category> categories = pagedCategories.getContent();

        if(categories.isEmpty()){
            throw new APIException("Category is Empty");
        }

        //Converting Category to Category DTO
        List<CategoryDTO> categoryDTO = categories.stream().map(CommonMapper.INSTANCE::toCategoryDTO).toList();

        CategoryResponse categoryResponse = new CategoryResponse();

        categoryResponse.setData(categoryDTO);
        categoryResponse.setPageNumber(pagedCategories.getNumber());
        categoryResponse.setPageSize(pagedCategories.getSize());
        categoryResponse.setTotalElements(pagedCategories.getTotalElements());
        categoryResponse.setTotalPages(pagedCategories.getTotalPages());
        categoryResponse.setLastPage(pagedCategories.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));

        return CommonMapper.INSTANCE.toCategoryDTO(category);
    }

    @Override
    public CategoryDTO updateCategory(Category category, Long categoryId) {

        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(()->new APIException("Category not found"));

        savedCategory.setCategoryName(category.getCategoryName());

        categoryRepository.save(savedCategory);

        return CommonMapper.INSTANCE.toCategoryDTO(savedCategory);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));

        List<Product> products = savedCategory.getProducts();

        for(Product product:products){
            product.setCategory(null);
        }

        categoryRepository.delete(savedCategory);

        return CommonMapper.INSTANCE.toCategoryDTO(savedCategory);
    }
}
