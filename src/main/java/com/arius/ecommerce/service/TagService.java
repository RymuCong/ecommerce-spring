package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.TagDTO;

import java.util.List;

public interface TagService {
    TagDTO addTag(TagDTO tagDTO);

    TagDTO getTagById(Long tagId);

    TagDTO updateTag(Long tagId, TagDTO tagDTO);

    void deleteTag(Long tagId);

    List<TagDTO> getAllTags();

    List<TagDTO> getTagsByProductId(Long productId);

    ProductDTO addTagToProduct(Long productId, TagDTO tagDTO);
}
