package com.arius.ecommerce.controller;

import com.arius.ecommerce.dto.TagDTO;
import com.arius.ecommerce.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("/admin/tag")
    public ResponseEntity<?> addTag(@RequestBody TagDTO tagDTO){
        return ResponseEntity.ok(tagService.addTag(tagDTO));
    }

    @PostMapping("/admin/tag/product/{productId}")
    public ResponseEntity<?> addTagToProduct(@PathVariable Long productId, @RequestBody TagDTO tagDTO){
        return ResponseEntity.ok(tagService.addTagToProduct(productId, tagDTO));
    }

    @GetMapping("/public/tag")
    public ResponseEntity<?> getAllTags(
            @RequestParam(name = "pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(name = "pageSize",defaultValue = "10",required = false) int pageSize,
            @RequestParam(name = "sortBy",defaultValue = "tagId",required = false) String sortBy,
            @RequestParam(name = "sortDir",defaultValue = "asc",required = false) String sortDir
    ){
        return ResponseEntity.ok(tagService.getAllTags(pageNumber, pageSize, sortBy, sortDir));
    }

    @GetMapping("/public/tag/{tagId}")
    public ResponseEntity<?> getTagById(@PathVariable Long tagId){
        return ResponseEntity.ok(tagService.getTagById(tagId));
    }

    @PatchMapping("/admin/tag/{tagId}")
    public ResponseEntity<?> updateTag(@PathVariable Long tagId, @RequestBody TagDTO tagDTO){
        return ResponseEntity.ok(tagService.updateTag(tagId, tagDTO));
    }

    @DeleteMapping("/admin/tag/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long tagId){
        return ResponseEntity.ok(tagService.deleteTag(tagId));
    }

    @GetMapping("/public/tag/product/{productId}")
    public ResponseEntity<?> getTagsByProductId(@PathVariable Long productId){
        return ResponseEntity.ok(tagService.getTagsByProductId(productId));
    }
}
