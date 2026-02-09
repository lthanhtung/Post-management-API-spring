package com.postmanagementapi.controller;

import com.postmanagementapi.heplper.ApiResponse;
import com.postmanagementapi.model.Tag;
import com.postmanagementapi.model.dto.request.TagFilterRequestDTO;
import com.postmanagementapi.model.dto.response.PageResponse;
import com.postmanagementapi.model.dto.response.TagResponseDTO;
import com.postmanagementapi.service.TagService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<PageResponse<TagResponseDTO>>> getAllTag(
            Pageable pageable,
            TagFilterRequestDTO tagFilter

    ){
        Page<TagResponseDTO> tags = this.tagService.getAllTag(pageable,tagFilter);

        return ApiResponse.success(PageResponse.from(tags));
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
