package com.bappi.userauthservice.controller;

import com.bappi.userauthservice.model.dto.AuthRequestDto;
import com.bappi.userauthservice.model.dto.UserInfoRequestDto;
import com.bappi.userauthservice.model.dto.UserInfoResponseDto;
import com.bappi.userauthservice.model.entity.UserInfo;
import com.bappi.userauthservice.service.AuthService;
import com.bappi.userauthservice.utils.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.bappi.userauthservice.config.ApiPath.*;
import static com.bappi.userauthservice.config.Constant.*;

@Slf4j
@RestController
@RequestMapping(API_USER)
public class UserInfoController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserInfoController(AuthService authService,AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    // User Registration API
    @PostMapping(value=API_POST_ADD_USER)
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> userRegister(@RequestBody UserInfoRequestDto requestDto) {
        log.info("Call Start userRegister method for user {}", requestDto.getUserName());

        // Validate input fields for empty values
        if (isNullOrEmpty(requestDto.getUserName())) {
            return buildErrorResponse(INVALID_USER_NAME_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        if (isNullOrEmpty(requestDto.getEmail())) {
            return buildErrorResponse(INVALID_EMAIL_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        // Check if username or email already exist in the system
        Optional<UserInfo> existingUserByUserName = authService.getByUserName(requestDto.getUserName());
        if (existingUserByUserName.isPresent()) {
            return buildErrorResponse(EXIST_USER_NAME_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        Optional<UserInfo> existingUserByEmail = authService.getByEmail(requestDto.getEmail());
        if (existingUserByEmail.isPresent()) {
            return buildErrorResponse(EXIST_EMAIL_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        // Try to add the new user
        try {
            authService.addUser(requestDto);
        } catch (Exception e) {
            log.error("Error occurred while registering user {}: {}", requestDto.getUserName(), e.getMessage());
            return buildErrorResponse(UNEXPECTED_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Successfully registered
        return buildSuccessResponse(CREATED_SUCCESSFULLY_MESSAGE, null);
    }

    // GetToken API
    @PostMapping(value = API_POST_USER_GET_TOKEN)
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getToken(@RequestBody AuthRequestDto requestDto) {
        log.info("Call Start getToken method for user {}", requestDto.getUserName());

        // Input validation: Username and password cannot be empty
        if (isNullOrEmpty(requestDto.getUserName())) {
            return buildErrorResponse(EMPTY_USER_NAME_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        if (isNullOrEmpty(requestDto.getPassword())) {
            return buildErrorResponse(EMPTY_EMAIL_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = null;
        try {
            // Authenticate the user using Username and Password
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getUserName(), requestDto.getPassword()));
        } catch (Exception e) {
            log.error("Authentication failed for user {}: {}", requestDto.getUserName(), e.getMessage());
            return buildErrorResponse(INVALID_USER_NAME_OR_PASSWORD_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        if (authentication.isAuthenticated()) {
            try {
                log.info("Generating token for user {}", requestDto.getUserName());
                // Generate token after successful authentication
                String token = authService.generateToken(requestDto.getUserName());
                return buildSuccessResponse(GET_TOKEN_SUCCESSFULLY_MESSAGE, new UserInfoResponseDto(token));
            } catch (Exception e) {
                log.error("Error generating token for user {}: {}", requestDto.getUserName(), e.getMessage());
                return buildErrorResponse(ERROR_TOKEN_GENERATING_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.error("User authentication failed for user {}", requestDto.getUserName());
            return buildErrorResponse(INVALID_USER_NAME_OR_PASSWORD_MESSAGE, HttpStatus.BAD_REQUEST);
        }
    }

    // Validate Token API
    @GetMapping(value = API_POST_VALIDATE)
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> validateToken(@RequestParam("token") String token) {
        log.info("Validating token: {}", token);

        // Check if the token is valid (not empty or null)
        if (isNullOrEmpty(token)) {
            log.error("Token is missing or empty.");
            return buildErrorResponse(EMPTY_TOKEN_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        // Validate the token using authService
        boolean isValid = authService.validateToken(token);
        if (!isValid) {
            log.error("Invalid token: {}", token);
            return buildErrorResponse(GET_TOKEN_INVALID_MESSAGE, HttpStatus.NOT_FOUND);
        }

        log.info("Token validated successfully.");
        return buildSuccessResponse(GET_TOKEN_VALID_MESSAGE, null);
    }


    private ResponseEntity<ApiResponse<UserInfoResponseDto>> buildSuccessResponse(String message, UserInfoResponseDto data) {
        ApiResponse<UserInfoResponseDto> response = new ApiResponse<>(SUCCESS_STATUS, message, data, null);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<ApiResponse<UserInfoResponseDto>> buildErrorResponse(String message, HttpStatus status) {
        ApiResponse<UserInfoResponseDto> response = new ApiResponse<>(FAILED_STATUS, message, null, null);
        return ResponseEntity.status(status).body(response);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

}
