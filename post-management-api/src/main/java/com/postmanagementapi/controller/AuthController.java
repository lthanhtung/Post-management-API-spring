package com.postmanagementapi.controller;

import com.postmanagementapi.heplper.ApiResponse;
import com.postmanagementapi.model.RefreshToken;
import com.postmanagementapi.model.User;
import com.postmanagementapi.model.dto.ExchangeTokenResponse;
import com.postmanagementapi.model.dto.request.LoginRequestDTO;
import com.postmanagementapi.model.dto.request.RegisterRequestDTO;
import com.postmanagementapi.model.dto.request.ResetPasswordRequest;
import com.postmanagementapi.model.dto.response.LoginResponseDTO;
import com.postmanagementapi.service.JwtService;
import com.postmanagementapi.service.RefreshTokenService;
import com.postmanagementapi.service.ResetTokenStore;
import com.postmanagementapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final ResetTokenStore resetTokenStore;


    @Value("${jwt.refresh-token.validity-in-seconds}")
    private Long refreshTokenExpiration;

    @PostMapping("/login")
    public ResponseEntity<?> postLogin(@Valid @RequestBody LoginRequestDTO request){

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        User currentUser = this.userService.findEmailUser(authentication.getName());

        String accessToken = this.jwtService.createAccessToken(authentication, currentUser.getId());

        String refreshToken = this.jwtService.createRefreshToken(currentUser);

        LoginResponseDTO response =  new LoginResponseDTO();
        response.setAccessToken(accessToken);
        response.setUser(
                new LoginResponseDTO.UserLogin(
                        currentUser.getId(),
                        authentication.getName(),
                        this.jwtService.getScope(authentication)
                )
        );
        response.setRefreshToken(refreshToken);

        ResponseCookie responseCookie = ResponseCookie
                .from("refreshtoken",refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        ApiResponse<LoginResponseDTO> finalResponse = new ApiResponse<>(
                HttpStatus.OK,"",response,""
        );
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body(finalResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> postRefreshToken(@RequestParam String refreshToken){
        ExchangeTokenResponse res = this.jwtService.handleExchangToken(refreshToken);
        return ApiResponse.success(res);
    }

    @PostMapping("/refresh-with-cookie")
    public ResponseEntity<?> refreshTokenWithCookie(
            @CookieValue(required = false) String refreshtoken
    ){
        ExchangeTokenResponse response = this.jwtService.handleExchangToken(refreshtoken);

        ResponseCookie responseCookie = ResponseCookie
                .from("refreshtoken", response.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        ApiResponse<ExchangeTokenResponse> finalData = new ApiResponse<>(
                HttpStatus.OK,"",response,""
        );

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body(finalData);
    }

    @PostMapping("register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequestDTO registerRequest
    ){
        this.userService.registerUser(registerRequest);
        return ApiResponse.success("user registration successful");
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAccount(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) auth.getPrincipal();
        String userId = jwt.getClaimAsString("id");
        String userName = jwt.getSubject();
        String role = jwt.getClaimAsString("role");

//        LoginResponseDTO.UserLogin userLogin = new LoginResponseDTO.UserLogin(
//                Long.valueOf(userId), userName, role
//        );
        LoginResponseDTO.UserLogin userLogin = new LoginResponseDTO.UserLogin();
        userLogin.setId(Long.valueOf(userId));
        userLogin.setRole(role);
        userLogin.setUsername(userName);

        return ApiResponse.success(userLogin);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> Logout(
            @CookieValue(required = false) String refreshtoken
            ){
        RefreshToken currentToken = this.refreshTokenService.findByToken(refreshtoken);
        this.refreshTokenService.deleteRefreshToken(currentToken.getId());

        ResponseCookie deleteCookie = ResponseCookie
                .from("refreshtoken",null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        ApiResponse<String> finalData = new ApiResponse<>(
                HttpStatus.OK,"","Logout success",""
        );

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,deleteCookie.toString()).body(finalData);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> lostPassword(@RequestBody String email){//cái này body dùng là text chú ý


        String resetToken = this.userService.forgotPassword(email);
        return ApiResponse.success("Reset Token:" +resetToken );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPassword){
        boolean isValid = resetTokenStore.verify(
                resetPassword.getEmail(),
                resetPassword.getResetToken()
        );

        if (!isValid){
            return ApiResponse.error(HttpStatus.BAD_REQUEST,"Reset token không đúng");
        }

        this.userService.resetPassword(resetPassword.getEmail(),resetPassword.getNewPassword());

        resetTokenStore.remove(resetPassword.getEmail());

        return ApiResponse.success("Đổi mật khẩu thành công");

    }


}
