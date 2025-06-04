package com.khata.auth.service.impl;

import com.khata.auth.dto.UserDTO;
import com.khata.auth.entity.User;
import com.khata.auth.repositories.UserRepo;
import com.khata.auth.service.UserService;
import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.mailVerification.service.MailVerificationService;
import com.khata.utils.EmailAndPhoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;
    private final MailVerificationService mailVerificationService;

    public UserServiceImpl(UserRepo userRepo, ModelMapper modelMapper, PasswordEncoder encoder, MailVerificationService mailVerificationService) {
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
        this.encoder = encoder;
        this.mailVerificationService = mailVerificationService;
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        checkEmailIfExists(userDTO.getEmail());
        checkPhoneNumberIfExists(userDTO.getPhoneNumber());
        user.setPassword(encodePassword(userDTO.getPassword()));
        User savedUser = userRepo.save(user);
        mailVerificationService.sendVerificationEmail(userDTO.getEmail());
        log.info("User created with email: {}", userDTO.getEmail());
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    @Transactional
    public UserDTO updateUser(UserDTO userDTO, Integer userId) {
        User user = getUserEntityById(userId);

        if (!user.getEmail().equals(userDTO.getEmail())) {
            checkEmailIfExists(userDTO.getEmail());
        }

        if (!user.getPhoneNumber().equals(userDTO.getPhoneNumber())) {
            checkPhoneNumberIfExists(userDTO.getPhoneNumber());
        }

        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(encodePassword(userDTO.getPassword()));
        }
        User updateUser = userRepo.save(user);
        log.info("User updated with ID: {}", userId);
        return modelMapper.map(updateUser, UserDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Integer userId) {
        User user = getUserEntityById(userId);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getUsers(Pageable pageable) {
        Page<User> users = userRepo.findAll(pageable);
        return users.map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUserByName(String name, Pageable pageable) {
        Page<User> users = userRepo.findByFullNameContainingIgnoreCase(name, pageable);
        return users.map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User user = getUserEntityById(userId);
        userRepo.delete(user);
        log.info("User deleted with ID: {}", userId);
    }

    private User getUserEntityById(Integer userId) {
        return userRepo.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userId));
    }

    private String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return encoder.encode(rawPassword);
    }

    private void checkEmailIfExists(String email) {
        if (!EmailAndPhoneUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (userRepo.findByEmail(email).isPresent()) {
            log.error("Email already exists: {}", email);
            throw new ResourceAlreadyExistsException("Email", email);
        }
    }

    private void checkPhoneNumberIfExists(String phoneNumber) {
        if (!EmailAndPhoneUtil.isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Invalid phone number format");
        }

        if (userRepo.findByPhoneNumber(phoneNumber).isPresent()) {
            log.error("Phone number already exists: {}", phoneNumber);
            throw new ResourceAlreadyExistsException("Phone number", phoneNumber);
        }
    }
}
