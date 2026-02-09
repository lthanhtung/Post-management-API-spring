package com.postmanagementapi.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterRequestDTO {

    private String name;

    private String address;

    private String email;

    private String roleName;

}
