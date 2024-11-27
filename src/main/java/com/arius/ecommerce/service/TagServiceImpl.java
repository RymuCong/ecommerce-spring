package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.TagDTO;
import com.arius.ecommerce.entity.Product;
import com.arius.ecommerce.entity.Tag;
import com.arius.ecommerce.exception.APIException;
import com.arius.ecommerce.repository.ProductRepository;
import com.arius.ecommerce.repository.TagRepository;
import com.arius.ecommerce.utils.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService{

    private final TagRepository tagRepository;

    private final ProductRepository productRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, ProductRepository productRepository) {
        this.tagRepository = tagRepository;
        this.productRepository = productRepository;
    }

    @Override
    public TagDTO addTag(TagDTO tagDTO) {
        Tag tag = CommonMapper.INSTANCE.toTag(tagDTO);
        tagRepository.save(tag);
        return CommonMapper.INSTANCE.toTagDTO(tag);
    }

    @Override
    public TagDTO getTagById(Long tagId) {
        Tag tag = tagRepository.findTagByTagId(tagId);
        if (tag == null){
            throw new APIException("Tag not found " + tagId);
        }
        return CommonMapper.INSTANCE.toTagDTO(tag);
    }

    @Override
    public TagDTO updateTag(Long tagId, TagDTO tagDTO) {
        Tag tag = tagRepository.findTagByTagId(tagId);
        if (tag == null){
            throw new APIException("Tag not found " + tagId);
        }
        tag.setTagName(tagDTO.getTagName());
        tagRepository.save(tag);
        return CommonMapper.INSTANCE.toTagDTO(tag);
    }

    @Override
    public void deleteTag(Long tagId) {
        Tag tag = tagRepository.findTagByTagId(tagId);
        if (tag == null){
            throw new APIException("Tag not found " + tagId);
        }
        tagRepository.delete(tag);
    }

    @Override
    public List<TagDTO> getAllTags() {
        return tagRepository.findAll().stream().map(CommonMapper.INSTANCE::toTagDTO).toList();
    }

    @Override
    public List<TagDTO> getTagsByProductId(Long productId) {
        Product product = productRepository.findProductByProductId(productId);
        if (product == null){
            throw new APIException("Product not found " + productId);
        }
        return tagRepository.findTagsByProductsProductId(productId).stream().map(CommonMapper.INSTANCE::toTagDTO).toList();
    }

    @Override
    public ProductDTO addTagToProduct(Long productId, TagDTO tagDTO) {
        Product product = productRepository.findProductByProductId(productId);
        if (product == null){
            throw new APIException("Product not found " + productId);
        }
        Tag tag = tagRepository.findTagByTagId(tagDTO.getTagId());
        if (tag == null){
            tag = CommonMapper.INSTANCE.toTag(tagDTO);
            tagRepository.save(tag);
        }
        // check if tag already exists
        final Tag finalTag = tag;
        if (product.getTags().stream().anyMatch(t -> t.getTagId().equals(finalTag.getTagId()))){
            throw new APIException("Tag already exists in product");
        }
        product.getTags().add(tag);
        productRepository.save(product);
        return CommonMapper.INSTANCE.toProductDTO(product);
    }
}
