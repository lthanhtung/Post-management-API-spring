package com.postmanagementapi.controller;

import com.postmanagementapi.heplper.ApiResponse;
import com.postmanagementapi.model.dto.request.PostRequestDTO;
import com.postmanagementapi.model.dto.response.PostResponseDTO;
import com.postmanagementapi.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<PostResponseDTO>>> getAllPost(){
        List<PostResponseDTO> posts = this.postService.getAllPost();
        return ApiResponse.success(posts);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<ApiResponse<PostResponseDTO>> getPostById(@PathVariable long id){
        PostResponseDTO post = this.postService.getPostById(id);

        return ApiResponse.success(post);
    }

    @PostMapping("/post")
    public ResponseEntity<ApiResponse<PostResponseDTO>> createPost(@Valid @RequestBody PostRequestDTO request){
        PostResponseDTO postResponseDTO = this.postService.createPost(request);

        return ApiResponse.success(postResponseDTO);
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<ApiResponse<PostResponseDTO>> updatePost(@Valid @RequestBody PostRequestDTO request,@PathVariable long id){
        PostResponseDTO post = this.postService.updatePost(request,id);

        return ApiResponse.success(post,"Cập nhập post thành công");
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable long id){
        this.postService.deletePost(id);
        return ApiResponse.success("Xóa post thành công");
    }

}
