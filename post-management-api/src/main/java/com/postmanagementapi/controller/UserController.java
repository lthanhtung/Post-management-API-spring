package com.postmanagementapi.controller;

import com.postmanagementapi.heplper.ApiResponse;
import com.postmanagementapi.model.User;
import com.postmanagementapi.model.dto.response.UserResponseDTO;
import com.postmanagementapi.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUser(){
        List<UserResponseDTO> userList = this.userService.getAllUser();
        return ApiResponse.success(userList);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable long id){
        UserResponseDTO dto = this.userService.getUserById(id);
        return ApiResponse.success(dto);
    }

    @PostMapping("/user")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@Valid @RequestBody User user){
       if (this.userService.findUserByEmail(user.getEmail()))
       {
           return ApiResponse.error(HttpStatus.BAD_REQUEST,"Email đã tồn tại");
       }

        UserResponseDTO userResponseDTO = this.userService.createUser(user);
        return ApiResponse.success(userResponseDTO,"Tạo mới User thành công");
    }

//    Tối về viết api full User và full app
    @PutMapping("/user/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> UpdateUser(
            @PathVariable long id,
            @Valid @RequestBody User userInput,
            BindingResult bindingResult){

        if (this.userService.findUserByEmail(userInput.getEmail())) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST,"Email đã tồn tại");
        }

        UserResponseDTO dto = this.userService.updateUser(id,userInput);

        return ApiResponse.success(dto,"Cập nhập user thành công");
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable long id){
      this.userService.deleteUser(id);

      return ApiResponse.success("Xóa User thành công");

    }

}
