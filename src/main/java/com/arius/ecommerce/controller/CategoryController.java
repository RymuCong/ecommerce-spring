package com.arius.ecommerce.controller;

import com.arius.ecommerce.config.AppConstants;
import com.arius.ecommerce.dto.CategoryDTO;
import com.arius.ecommerce.dto.response.BasePagination;
import com.arius.ecommerce.entity.Category;
import com.arius.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/admin/category")
    public ResponseEntity<?> createCategory(@Valid @RequestBody Category category){
        CategoryDTO categoryDTO = categoryService.createCategory(category);
        return new ResponseEntity<>(categoryDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/category")
    public ResponseEntity<BasePagination<CategoryDTO>> getCategories(
            @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) int pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_CATEGORY_BY,required = false) String sortBy,
            @RequestParam(name = "sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir
    ){
        BasePagination<CategoryDTO> categoryResponse = categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortDir);

        return new  ResponseEntity<>(categoryResponse,HttpStatus.OK);
    }

    @GetMapping("/public/category/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable("categoryId") Long categoryId){

        CategoryDTO categoryDTO = categoryService.getCategoryById(categoryId);

        return new ResponseEntity<>(categoryDTO,HttpStatus.OK);
    }

    @PatchMapping("/admin/category/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody Category category, @PathVariable("categoryId") Long categoryId){

        CategoryDTO categoryDTO = categoryService.updateCategory(category,categoryId);

        return new ResponseEntity<>(categoryDTO,HttpStatus.OK);
    }

    @DeleteMapping("/admin/category/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable("categoryId") Long categoryId){

        CategoryDTO deleteCategory = categoryService.deleteCategory(categoryId);

        return new ResponseEntity<>(deleteCategory,HttpStatus.OK);

    }
}
