package com.postmanagementapi.service;

import com.postmanagementapi.heplper.exception.ResourceNotFoundException;
import com.postmanagementapi.model.User;
import com.postmanagementapi.model.dto.request.RegisterRequestDTO;
import com.postmanagementapi.model.dto.request.UserFilterRequestDTO;
import com.postmanagementapi.model.dto.response.UserResponseDTO;
import com.postmanagementapi.repository.UserRepository;
import com.postmanagementapi.service.Email.EmailService;
import com.postmanagementapi.service.specification.UserSpecification;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final ResetTokenStore resetTokenStore;

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

    public User findEmailUser (String email){
        return this.userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Khong tim thay email")
                );
    }

    public Page<UserResponseDTO> getAllUser(
            Pageable pageable,
            UserFilterRequestDTO requestFilter
    ){

        Specification<User> specs = Specification.allOf(
                UserSpecification.hasName(requestFilter),
                UserSpecification.hasEmail(requestFilter)
        );
      Page<UserResponseDTO> users = this.userRepository.findAll(specs,pageable).map(
              user -> {
                  return ConverUsertoDTO(user);
              }
      );
      return users;
    }

    public UserResponseDTO getUserById(long id){
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tim thấy user với id:" + id));

        return ConverUsertoDTO(user);
    }

    public UserResponseDTO createUser(User user){
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);

        return ConverUsertoDTO(this.userRepository.save(user));
    }

    public UserResponseDTO updateUser(long id, User user){
        User userInDB = this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id " + id));

        userInDB.setUserName(user.getUserName());
        userInDB.setEmail(user.getEmail());
        String hashPassword = passwordEncoder.encode(user.getPassword());
        userInDB.setPassword(hashPassword);
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

    public void registerUser(RegisterRequestDTO registerRequest){
        if (this.userRepository.existsByEmail(registerRequest.getEmail())){
            throw  new ResourceNotFoundException("This email has already been registered");
        }

        String role = "USER";
        String hashPassword = passwordEncoder.encode(registerRequest.getPassword());

        User userRegister = new User();
        userRegister.setUserName(registerRequest.getName());
        userRegister.setRole(role);
        userRegister.setEmail(registerRequest.getEmail());
        userRegister.setPassword(hashPassword);

        this.userRepository.save(userRegister);
        String subject = "ĐĂNG KÝ";

        String content =
        """
            Chào %s,
            Chúc mừng bạn đã đăng ký tài khoản thành công!
            Tài khoản của bạn đã sẵn sàng để sử dụng.
            Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi.
            Trân trọng,
            Đội ngũ hệ thống
        """.formatted(userRegister.getUserName());

        this.emailService.notifyUserRegistered(userRegister.getEmail(),subject,content);

    }

    public String forgotPassword(String email){
//        Tìm email
        User userInDB = findEmailUser(email);

        String resetToken = String.format(
                "%6d",new SecureRandom().nextInt(1_000_000)
        );

        resetTokenStore.save(email,resetToken);

        String subject = "RESET PASSWORD TOKEN";

        String content =
                """
                    Mã khôi phục tài khoản của bạn là: %s
                    Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi.
                    Trân trọng,
                    Đội ngũ hệ thống
                """.formatted(resetToken);
        this.emailService.notifyUserRegistered(email,subject,content);


        return resetToken;
    }

    public void resetPassword(String email, String newPassword){
        User user = this.findEmailUser(email);
        String hashPassword = this.passwordEncoder.encode(newPassword);

        user.setPassword(hashPassword);

        String subject = "RESET PASSWORD";

        String content =
                """
                    Mật khẩu của bạn đã được thay đổi thành công
                    Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi.
                    Trân trọng,
                    Đội ngũ hệ thống
                """;

        this.emailService.notifyUserRegistered(user.getEmail(),subject,content);

        this.userRepository.save(user);
    }

}
