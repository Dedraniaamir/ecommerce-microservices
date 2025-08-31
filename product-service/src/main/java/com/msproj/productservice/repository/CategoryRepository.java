package com.msproj.productservice.repository;

import com.msproj.productservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; /**
 * Category Repository
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentIsNull();  // Root categories
    List<Category> findByParent(Category parent);
    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c WHERE SIZE(c.products) >= :minProducts")
    List<Category> findCategoriesWithMinimumProducts(@Param("minProducts") int minProducts);

    @Query("SELECT c FROM Category c JOIN c.tags t WHERE t IN :tags")
    List<Category> findByTagsIn(@Param("tags") List<String> tags);
}
