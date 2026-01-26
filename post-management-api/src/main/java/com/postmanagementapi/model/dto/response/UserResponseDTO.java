package com.postmanagementapi.model.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private Long id;

    private  String userName;

    private String email;

    private String role;
}
