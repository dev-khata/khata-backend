package com.khata.auth.controller;

import com.khata.auth.dto.UserDTO;
import com.khata.auth.entity.User;
import com.khata.auth.payload.JwtAuthRequest;
import com.khata.auth.payload.JwtAuthResponse;
import com.khata.auth.service.AuthService;
import com.khata.auth.service.UserService;
import com.khata.payload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/")
@Tag(name = "Auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<?>> registerUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO registerUser = userService.createUser(userDTO);
        ApiResponse<UserDTO> response = new ApiResponse<>(registerUser, HttpStatus.CREATED.value(), "User Registered Successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/user-login")
    public ResponseEntity<ApiResponse<?>> loginUser(@Valid @RequestBody JwtAuthRequest jwtAuthRequest) {
        User user = authService.findUserEntityByEmail(jwtAuthRequest.getUsername());
        if (!user.isVerified()) {
            UserDTO userDTO = authService.mapUserEntityToDTO(user);
            return ResponseEntity.ok(new ApiResponse<>(userDTO, HttpStatus.FORBIDDEN.value(), "Your account is not verified. Please verify your email before logging in"));
        } else {
            JwtAuthResponse jwtAuthResponse = authService.authenticateUserAndGenerateToken(jwtAuthRequest);
            return ResponseEntity.ok(new ApiResponse<>(jwtAuthResponse, HttpStatus.OK.value()));
        }
    }

}
