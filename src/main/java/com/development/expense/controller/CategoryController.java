package com.development.expense.controller;

import com.development.expense.dto.CategoryDto;
import com.development.expense.entity.CategoryEntity;
import com.development.expense.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<List<CategoryDto>> categories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryEntity> findById(@PathVariable Long id){
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping("")
    public ResponseEntity<String> add(@RequestBody CategoryEntity category){
        return ResponseEntity.ok(categoryService.add(category));
    }

    @PutMapping("")
    public ResponseEntity<String> update(@RequestBody CategoryEntity category){
        return ResponseEntity.ok(categoryService.update(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return ResponseEntity.ok(categoryService.delete(id));
    }
}
