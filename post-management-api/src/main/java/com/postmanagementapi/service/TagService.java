package com.postmanagementapi.service;

import com.postmanagementapi.heplper.exception.ResourceNotFoundException;
import com.postmanagementapi.model.Tag;
import com.postmanagementapi.model.dto.response.TagResponseDTO;
import com.postmanagementapi.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TagService {


    private final TagRepository tagRepository;

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

    public List<TagResponseDTO> getAllTag(){
        List<TagResponseDTO> tags = this.tagRepository.findAll().stream()
                                    .map(tag -> convertTagToDTO(tag)).collect(Collectors.toList());
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
        this.tagRepository.deleteById(id);
    }
}
