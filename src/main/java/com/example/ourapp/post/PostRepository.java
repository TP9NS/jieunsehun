package com.example.ourapp.post;

import com.example.ourapp.entity.Category;
import com.example.ourapp.entity.Post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
   // 카테고리 계층 데이터를 가져오기
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parentCategory")
    List<Category> findAllCategoriesWithHierarchy();

    // 기존 카테고리 이름만 가져오기
    @Query("SELECT DISTINCT p.category.name FROM Post p")
    List<String> findDistinctCategories();
    
    @Query("SELECT p FROM Post p JOIN FETCH p.category c LEFT JOIN FETCH c.parentCategory WHERE p.id = :id")
    Optional<Post> findPostWithCategoryAndParent(@Param("id") Long id);
    
    @Query("SELECT p FROM Post p JOIN p.category c WHERE c.parentCategory.name = :parentCategoryName")
    List<Post> findPostsByParentCategoryName(@Param("parentCategoryName") String parentCategoryName);

    @Query("SELECT p FROM Post p " +
              "JOIN p.category c " +
              "LEFT JOIN c.parentCategory pc " +
              "WHERE pc.name = :parentCategoryName OR c.name = :parentCategoryName")
       List<Post> findByParentCategoryName(@Param("parentCategoryName") String parentCategoryName);
    
    Page<Post> findByParentCategoryNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            String parentCategoryName, String title, Pageable pageable);
    
    List<Post> findByUsername(String username);
}
