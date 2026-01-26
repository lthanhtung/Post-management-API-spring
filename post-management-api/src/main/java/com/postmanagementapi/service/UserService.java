package com.postmanagementapi.service;

import com.postmanagementapi.heplper.exception.ResourceNotFoundException;
import com.postmanagementapi.model.User;
import com.postmanagementapi.model.dto.response.UserResponseDTO;
import com.postmanagementapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponseDTO ConverUsertoDTO(User user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public boolean findUserByEmail(String email){
        return this.userRepository.existsByEmail(email);
    }

    public List<UserResponseDTO> getAllUser(){
        List<UserResponseDTO> users = this.userRepository.findAll().stream().map(
                user -> {
                    return ConverUsertoDTO(user);
                }).collect(Collectors.toList());
        return users;
    }

    public UserResponseDTO getUserById(long id){
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tim thấy user với id:" + id));

        return ConverUsertoDTO(user);
    }

    public UserResponseDTO createUser(User user){
        return ConverUsertoDTO(this.userRepository.save(user));
    }

    public UserResponseDTO updateUser(long id, User user){
        User userInDB = this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id " + id));

        userInDB.setUserName(user.getUserName());
        userInDB.setEmail(user.getEmail());
        userInDB.setPassword(user.getPassword());
        userInDB.setRole(user.getRole());

        return ConverUsertoDTO(this.userRepository.save(userInDB));
    }

    public void deleteUser(long id){
        if (this.userRepository.existsById(id)) {
            this.userRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("Không tìm thấy id");
        }
    }



}
