package com.development.expense.repository;

import com.development.expense.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    CategoryEntity findByName(String name);

//    @Query(value = "SELECT cat FROM CategoryEntity cat WHERE cat.name =:name")
//    CategoryEntity findByCategoryName(@Param("name") String name);
}