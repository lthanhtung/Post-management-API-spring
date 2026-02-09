package com.postmanagementapi.model.dto;

import com.postmanagementapi.model.dto.response.LoginResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType="Bearer";

    private LoginResponseDTO.UserLogin user;
}
