package com.postmanagementapi.service;

import com.postmanagementapi.heplper.exception.ResourceNotFoundException;
import com.postmanagementapi.model.Post;
import com.postmanagementapi.model.Tag;
import com.postmanagementapi.model.dto.request.TagFilterRequestDTO;
import com.postmanagementapi.model.dto.response.TagResponseDTO;
import com.postmanagementapi.repository.PostRepository;
import com.postmanagementapi.repository.TagRepository;
import com.postmanagementapi.service.specification.TagSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TagService {


    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public TagResponseDTO convertTagToDTO(Tag tag){
        return TagResponseDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .outputPosts(
                        tag.getPosts() == null ? List.of() : tag.getPosts().stream()
                                .map(post -> {
                                    return new TagResponseDTO.OutputPost(post.getId(),post.getTitle());
                                }).toList()
                )
                .build();
    }

    public Page<TagResponseDTO> getAllTag(
            Pageable pageable,
            TagFilterRequestDTO tagFilter
    ){

        String nameType = "fafsfs";


        Specification<Tag> specs = Specification.allOf(
                "like".equalsIgnoreCase(nameType)
                        ? TagSpecification.hasNameLike(tagFilter)
                        : TagSpecification.hasName(tagFilter)
        );

        Page<TagResponseDTO> tags = this.tagRepository.findAll(specs,pageable).map(
                tag -> {
                    return  new TagResponseDTO(tag.getId(),tag.getName(),null);
                }
        );
        return tags;
    }

    public TagResponseDTO createTag(Tag tagInput){

        return convertTagToDTO(this.tagRepository.save(tagInput));
    }


    public TagResponseDTO getTagById(long id) {
        Tag tag = this.tagRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy tag với id: " + id));

        return convertTagToDTO(tag);
    }

    public TagResponseDTO updateTag(long id,Tag tagInput) {
        Tag tagInDB = this.tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("không tìm thấy tag với id: "+ id));

        tagInDB.setName(tagInput.getName());

        return this.convertTagToDTO(this.tagRepository.save(tagInDB));

    }

    public void deleteTag(long id) {

        Tag tag = this.tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tag với id: " + id));

        List<Post> posts = this.postRepository.findByTagsContains(tag);

        for (Post post : posts){
            post.getTags().remove(tag);

            this.postRepository.save(post);
        }


        this.tagRepository.deleteById(id);
    }
}
