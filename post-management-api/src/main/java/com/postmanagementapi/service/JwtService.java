package com.postmanagementapi.service;

import com.postmanagementapi.heplper.exception.ResourceNotFoundException;
import com.postmanagementapi.model.RefreshToken;
import com.postmanagementapi.model.User;
import com.postmanagementapi.model.dto.ExchangeTokenResponse;
import com.postmanagementapi.model.dto.response.LoginResponseDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;

    private final JwtEncoder jwtEncoder;

    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access-token-validity-in-seconds}")
    private String accessTokenExpiration;

    public String getScope(Authentication authentication){
        if (authentication !=null){
            // ghép các quyền thành 1 string: "ROLE_USER ROLE_ADMIN"
            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
    //                    .filter(role -> role.startsWith("ROLE_"))
                    .collect(Collectors.joining(" "));

            return scope;

        }
        return "UNKNOWN";
    }


    public String createAccessToken(Authentication authentication,long userId){
        Instant now = Instant.now();
        Instant validity = now.plus(Long.valueOf(accessTokenExpiration), ChronoUnit.SECONDS);

        String scope = this.getScope(authentication);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("id",userId)
                .claim("role",scope)
                .build();
        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(header,claims)).getTokenValue();
    }


    public String generateSecurityToken(){
        byte[] randomByte = new byte[64];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomByte);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomByte);
    }


    @Value("${jwt.refresh-token.validity-in-seconds}")
    private String refreshtokenExpiration;

    public String createRefreshToken(User user){
        Instant now = Instant.now();
        Instant validity = now.plus(Long.valueOf(refreshtokenExpiration),ChronoUnit.SECONDS);

        String token = generateSecurityToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setCreateAt(now);
        refreshToken.setExpiredAt(validity);
        refreshToken.setUser(user);

        this.refreshTokenService.createRefreshToken(refreshToken);
        return token;
    }

    public ExchangeTokenResponse handleExchangToken(String inputToken){
        RefreshToken currentRefreshToken = this.refreshTokenService.findByToken(inputToken);

        Instant now = Instant.now();

        if (now.isAfter(currentRefreshToken.getExpiredAt())) {
            throw  new ResourceNotFoundException("Refresh token is expired");
        }

        User currentUser = currentRefreshToken.getUser();
        String newRefreshToken = this.createRefreshToken(currentUser);

        Instant validity = now.plus(Long.valueOf(accessTokenExpiration),ChronoUnit.SECONDS);
        String scope = "ROLE_" + currentUser.getRole();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(currentUser.getEmail())
                .claim("id",currentUser.getId())
                .claim("scope",scope)
                .build();

        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();

        String accessToken = this.jwtEncoder.encode(
                JwtEncoderParameters.from(header,claims)
        ).getTokenValue();

        ExchangeTokenResponse exToken = new ExchangeTokenResponse();
        exToken.setAccessToken(accessToken);
        exToken.setRefreshToken(newRefreshToken);
        exToken.setUser( new LoginResponseDTO.UserLogin(
                currentUser.getId(),
                currentUser.getEmail(),
                scope
        ));

        this.refreshTokenService.deleteRefreshToken(currentRefreshToken.getId());

        return exToken;
    }



}
