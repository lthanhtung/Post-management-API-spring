package com.postmanagementapi.model.dto.response;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponseDTO {

    private Long id;

    private String name;

    private List<OutputPost> outputPosts;


    @Getter
    @Setter
    @AllArgsConstructor
    public static class OutputPost{
        private Long id;

        private String title;

    }
}


