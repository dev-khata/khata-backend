package com.khata.auth.service;

import com.khata.auth.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO, Integer userId);

    UserDTO getUserById(Integer userId);

    Page<UserDTO> getUsers(Pageable pageable);

    Page<UserDTO> searchUserByName(String name, Pageable pageable);

    void deleteUser(Integer userId);

    Integer getCurrentUserId();
}
