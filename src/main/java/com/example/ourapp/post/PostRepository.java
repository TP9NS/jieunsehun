package com.example.ourapp.post;

import com.example.ourapp.entity.Category;
import com.example.ourapp.entity.Post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	// 카테고리 계층 데이터를 가져오기
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parentCategory")
    List<Category> findAllCategoriesWithHierarchy();

    // 기존 카테고리 이름만 가져오기
    @Query("SELECT DISTINCT p.category.name FROM Post p")
    List<String> findDistinctCategories();
}
