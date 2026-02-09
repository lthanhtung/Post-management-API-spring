package com.postmanagementapi.service;

import com.postmanagementapi.heplper.exception.ResourceNotFoundException;
import com.postmanagementapi.model.RefreshToken;
import com.postmanagementapi.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public void createRefreshToken(RefreshToken refreshToken){
        this.repository.save(refreshToken);
    }

    public RefreshToken findByToken(String token){
        return this.repository.findByToken(token).orElseThrow(
                () -> new ResourceNotFoundException("Token not found")
        );
    }

    public void deleteRefreshToken(Long id){
        this.repository.deleteById(id);
    }


}
