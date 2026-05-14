package com.khata.auth.service;

import com.khata.auth.dto.UserDTO;
import com.khata.auth.entity.User;
import com.khata.auth.payload.JwtAuthRequest;
import com.khata.auth.payload.JwtAuthResponse;
import com.khata.auth.repositories.UserRepo;
import com.khata.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenService jwtTokenService;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    /**
     * Constructs the AuthService with necessary dependencies.
     *
     * @param authenticationManager The authentication manager for authenticating users.
     * @param userDetailsService    The service to load user details for authentication.
     * @param jwtTokenService       The service for generating JWT tokens.
     * @param userRepo              The repository to interact with the User entity.
     * @param modelMapper           The model mapper to convert between entities and DTOs.
     */
    public AuthService(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtTokenService jwtTokenService,
            UserRepo userRepo,
            ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenService = jwtTokenService;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    /**
     * Authenticates the user credentials and generates a JWT token if successful.
     *
     * @param jwtAuthRequest The request containing the user's username and password.
     * @return A JWT authentication response containing the token and user details.
     * @throws ApiException If authentication fails or if the user is not found.
     */
    public JwtAuthResponse authenticateUserAndGenerateToken(JwtAuthRequest jwtAuthRequest) {
        authenticateUserCredentials(jwtAuthRequest.getUsername(), jwtAuthRequest.getPassword());
        UserDetails userDetails = loadUserDetailsByUsername(jwtAuthRequest.getUsername());
        String token = generateJwtTokenForUser(userDetails);
        UserDTO userDTO = mapUserEntityToDTO(findUserEntityByEmail(jwtAuthRequest.getUsername()));
        return new JwtAuthResponse(token, userDTO);
    }

    public JwtAuthResponse autoLoginAfterVerification(String email) {
        UserDetails userDetails = loadUserDetailsByUsername(email);
        String token = generateJwtTokenForUser(userDetails);
        User user = findUserEntityByEmail(email);
        UserDTO userDTO = mapUserEntityToDTO(user);
        return new JwtAuthResponse(token, userDTO);
    }

    public User findUserEntityByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(
                () -> new ApiException("User not found"));
    }

    public UserDTO mapUserEntityToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    /**
     * Authenticates the user's credentials using the provided username and password.
     *
     * @param username The username of the user to authenticate.
     * @param password The password of the user to authenticate.
     * @throws ApiException If the credentials are invalid.
     */
    private void authenticateUserCredentials(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            log.info("User {} authenticated successfully", username);
        } catch (BadCredentialsException e) {
            log.error("Invalid authentication attempt for user {}", username);
            throw new ApiException("Invalid username or password");
        }
    }

    private UserDetails loadUserDetailsByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    private String generateJwtTokenForUser(UserDetails userDetails) {
        return jwtTokenService.generateToken(userDetails);
    }
}
