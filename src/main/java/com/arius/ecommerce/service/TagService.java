package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.TagDTO;
import com.arius.ecommerce.dto.response.BasePagination;

import java.util.List;

public interface TagService {
    TagDTO addTag(TagDTO tagDTO);

    TagDTO getTagById(Long tagId);

    TagDTO updateTag(Long tagId, TagDTO tagDTO);

    TagDTO deleteTag(Long tagId);

    BasePagination<TagDTO> getAllTags(int page, int size, String sortBy, String sortDir);

    List<TagDTO> getTagsByProductId(Long productId);

    ProductDTO addTagToProduct(Long productId, TagDTO tagDTO);
}
