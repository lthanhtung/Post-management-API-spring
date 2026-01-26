package com.postmanagementapi.controller;

import com.postmanagementapi.heplper.ApiResponse;
import com.postmanagementapi.model.Tag;
import com.postmanagementapi.model.dto.response.TagResponseDTO;
import com.postmanagementapi.service.TagService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<TagResponseDTO>>> getAllTag(){
        List<TagResponseDTO> tags = this.tagService.getAllTag();

        return ApiResponse.success(tags);
    }

    @GetMapping("/tag/{id}")
    public ResponseEntity<ApiResponse<TagResponseDTO>> getTagById(@PathVariable long id){
        TagResponseDTO tag = this.tagService.getTagById(id);

        return ApiResponse.success(tag);
    }

    @PostMapping("/tag")
    public ResponseEntity<ApiResponse<TagResponseDTO>> createTag(@Valid @RequestBody Tag tagInput){
        TagResponseDTO tag = this.tagService.createTag(tagInput);

        return ApiResponse.success(tag);
    }

    @PutMapping("/tag/{id}")
    public ResponseEntity<ApiResponse<TagResponseDTO>> updateTag(@Valid @RequestBody Tag tagInput,@PathVariable long id){
        TagResponseDTO tag = this.tagService.updateTag(id,tagInput);

        return ApiResponse.success(tag,"Tạo mới tag thành công");
    }

    @DeleteMapping("/tag/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTag(@PathVariable long id){

        this.tagService.deleteTag(id);
        return ApiResponse.success("Xóa Tag thành công");
    }

}
