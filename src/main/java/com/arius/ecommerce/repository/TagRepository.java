package com.arius.ecommerce.repository;

import com.arius.ecommerce.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findTagByTagId(Long tagId);

    List<Tag> findTagsByProductsProductId(Long productId);
}
