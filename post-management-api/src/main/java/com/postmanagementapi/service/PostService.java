package com.postmanagementapi.service;

import com.postmanagementapi.heplper.exception.ResourceNotFoundException;
import com.postmanagementapi.model.Post;
import com.postmanagementapi.model.Tag;
import com.postmanagementapi.model.User;
import com.postmanagementapi.model.dto.request.PostRequestDTO;
import com.postmanagementapi.model.dto.response.PostResponseDTO;
import com.postmanagementapi.repository.PostRepository;
import com.postmanagementapi.repository.TagRepository;
import com.postmanagementapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    public Post convertRequestToPost(PostRequestDTO request,User user){
        Post post = new Post();
        post.setContent(request.getContent());
        post.setTitle(request.getTitle());
        post.setUser(user);

        return post;

    }

    public PostResponseDTO convertPostToDTO(Post post){
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .user( post.getUser() == null ? null : new PostResponseDTO.UserOutput(post.getUser().getId(),post.getUser().getUserName()))

                .tags(post.getTags() == null ? List.of() : post.getTags().stream().map(
                        tag -> new PostResponseDTO.TagOut(tag.getId(),tag.getName())
                ).collect(Collectors.toList()))
                .build();
    }


    public List<PostResponseDTO> getAllPost(){
        return this.postRepository.findAll().stream().map(
                post -> convertPostToDTO(post)
        ).collect(Collectors.toList());

    }

    public PostResponseDTO getPostById(long id){
        Post post = this.postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Không tìm thấy post với id " + id)
        );

        return convertPostToDTO(post);
    }


    public PostResponseDTO createPost(PostRequestDTO request) {
        User user = this.userRepository.findById(request.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id" + request.getUser().getId()));

        List<Tag> tags = request.getTags() == null ? List.of() : request.getTags().stream()
                .map(tagInput -> this.tagRepository.findByName(tagInput.getName()).orElseGet(
                        () -> {
                            Tag NewTag = new Tag();
                            NewTag.setName(tagInput.getName());
                            return this.tagRepository.save(NewTag);
                        }
                )).collect(Collectors.toList());

        Post post = convertRequestToPost(request,user);
        post.setTags(tags);

        return convertPostToDTO(this.postRepository.save(post));
    }


    public PostResponseDTO updatePost(PostRequestDTO request,long id) {
            Post postInDB = this.postRepository.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException("Không tìm thấy post với id: " + id)
            );

        postInDB.setTitle(request.getTitle());
        postInDB.setContent(request.getContent());

            if (request.getTags() !=null){
                List<Tag> tags = request.getTags().stream().map(
                        tagInput -> this.tagRepository.findByName(tagInput.getName()).orElseGet(
                                () -> {
                                    Tag NewTag = new Tag();
                                    NewTag.setName(tagInput.getName());
                                    return this.tagRepository.save(NewTag);
                                }
                        )).collect(Collectors.toList());

                postInDB.setTags(tags);
            }

            return convertPostToDTO(this.postRepository.save(postInDB));
    }

    public void deletePost(long id) {
        this.postRepository.deleteById(id);
    }
}
