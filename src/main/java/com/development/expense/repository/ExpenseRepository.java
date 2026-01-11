package com.development.expense.repository;

import com.development.expense.entity.ExpenseEntity;
import com.development.expense.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    List<ExpenseEntity> findAllByUserIdAndStatus(Long id, StatusEnum status);
}
