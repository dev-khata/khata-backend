package com.khata.auth.controller;

import com.khata.auth.dto.UserDTO;
import com.khata.auth.service.UserService;
import com.khata.payload.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO){
        UserDTO user = userService.createUser(userDTO);
        ApiResponse<UserDTO> response = new ApiResponse<>(user, HttpStatus.CREATED.value(), "User Created Successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getUsers(Pageable pageable){
        Page<UserDTO> userDTOPage = userService.getUsers(pageable);
        return ResponseEntity.ok(new ApiResponse<>(userDTOPage, HttpStatus.OK.value()));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@Valid @RequestBody UserDTO userDTO, @PathVariable Integer userId){
        UserDTO updatedUser = userService.updateUser(userDTO, userId);
        return ResponseEntity.ok(new ApiResponse<>(updatedUser, HttpStatus.OK.value()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Integer userId){
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse<>(userDTO, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Integer userId){
        userService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value(), "User Deleted Successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> searchUserByFullName(@RequestParam String keyword, Pageable pageable){
        Page<UserDTO> userDTOPage = userService.searchUserByName(keyword, pageable);
        return ResponseEntity.ok(new ApiResponse<>(userDTOPage, HttpStatus.OK.value()));
    }
}
