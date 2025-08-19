package com.cashcoach.backend.controller;

import com.cashcoach.backend.dto.CategoryDTO;
import com.cashcoach.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> userCategories = categoryService.getUserCategories();
        return ResponseEntity.ok(userCategories);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByTypeForCurrentUser(@PathVariable String type) {
        List<CategoryDTO> categoriesByType = categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.ok(categoriesByType);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }


}
