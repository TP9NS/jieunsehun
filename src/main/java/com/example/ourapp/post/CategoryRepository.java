package com.example.ourapp.post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ourapp.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findByParentCategoryId(Long parentId);
	 List<Category> findByParentCategoryIsNull();
}
