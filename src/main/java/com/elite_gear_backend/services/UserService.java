package com.elite_gear_backend.services;

import com.elite_gear_backend.dto.CredentialsDto;
import com.elite_gear_backend.dto.SignUpDto;
import com.elite_gear_backend.dto.UserDTO;
import com.elite_gear_backend.dto.UserProfileDto;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.enums.Role;
import com.elite_gear_backend.exceptions.AppException;
import com.elite_gear_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.nio.CharBuffer;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO login(CredentialsDto credentialsDto) {
        User user = userRepository.findByEmail(credentialsDto.getEmail())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            return toUserDto(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDTO register(SignUpDto userDto) {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());

        if (optionalUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        User user = signUpToUser(userDto);
        user.setType(Role.USER);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));

        User savedUser = userRepository.save(user);

        return toUserDto(savedUser);
    }

    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return toUserDto(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDTO) {
            String email = ((UserDTO) authentication.getPrincipal()).getEmail();
            return userRepository.findByEmail(email).orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        }
        throw new AppException("User not logged in", HttpStatus.UNAUTHORIZED);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserDTO toUserDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .type(user.getType())
                .build();
    }

    private User signUpToUser(SignUpDto signUpDto) {
        User user = new User();
        user.setName(signUpDto.getName());
        user.setSurname(signUpDto.getSurname());
        user.setEmail(signUpDto.getEmail());
        return user;
    }

    public UserProfileDto getUserProfile(User user) {
        return UserProfileDto.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .build();
    }
}
