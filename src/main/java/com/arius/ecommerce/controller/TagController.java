package com.arius.ecommerce.controller;

import com.arius.ecommerce.dto.TagDTO;
import com.arius.ecommerce.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/public/tag")
    public ResponseEntity<?> getAllTags(){
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/public/tag/{tagId}")
    public ResponseEntity<?> getTagById(@PathVariable Long tagId){
        return ResponseEntity.ok(tagService.getTagById(tagId));
    }

    @PutMapping("/admin/tag/{tagId}")
    public ResponseEntity<?> updateTag(@PathVariable Long tagId, @RequestBody TagDTO tagDTO){
        return ResponseEntity.ok(tagService.updateTag(tagId, tagDTO));
    }

    @DeleteMapping("/admin/tag/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long tagId){
        tagService.deleteTag(tagId);
        return ResponseEntity.ok("Tag deleted successfully");
    }

    @GetMapping("/public/tag/product/{productId}")
    public ResponseEntity<?> getTagsByProductId(@PathVariable Long productId){
        return ResponseEntity.ok(tagService.getTagsByProductId(productId));
    }
}
