package com.bappi.userauthservice.controller;

import com.bappi.userauthservice.model.dto.AuthRequestDto;
import com.bappi.userauthservice.model.dto.UserInfoRequestDto;
import com.bappi.userauthservice.model.dto.UserInfoResponseDto;
import com.bappi.userauthservice.service.AuthService;
import com.bappi.userauthservice.service.JwtTokenService;
import com.bappi.userauthservice.utils.ApiResponse;
import com.bappi.userauthservice.utils.error.APIErrorCode;
import com.bappi.userauthservice.utils.error.CustomException;
import com.bappi.userauthservice.utils.error.ServiceExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.bappi.userauthservice.config.ApiPath.*;
import static com.bappi.userauthservice.config.Constant.*;

@Slf4j
@RestController
@RequestMapping(API_USER)
public class UserInfoController {

    private final AuthService authService;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserInfoController(AuthService authService, JwtTokenService jwtTokenService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
    }

    // User Registration API
    @PostMapping(value=API_POST_ADD_USER)
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> userRegister(@RequestBody UserInfoRequestDto requestDto){
        log.info("Call Start userRegister method for user {}",requestDto.getEmail());
        ServiceExceptionHandler<String> dataHandler = () ->  authService.addUser(requestDto);
        dataHandler.executeHandler();
        return buildSuccessResponse(GET_TOKEN_VALID_MESSAGE,null);
    }

    // GetToken API
    @PostMapping(value = API_POST_USER_GET_TOKEN)
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getToken(@RequestBody AuthRequestDto requestDto){

        if (requestDto.getUserName().isEmpty()) {
            throw new CustomException(APIErrorCode.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
        }
        if (requestDto.getPassword().isEmpty()) {
            throw new CustomException(APIErrorCode.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = null;
        log.info("Call Start authenticateAndGetToken method for user {}",requestDto.getUserName());
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getUserName(),
                    requestDto.getPassword()));
        }catch (Exception e) {
            log.error("Invalid username or password {}",e.getMessage());
            return buildErrorResponse(GET_USER_FAILED_MESSAGE, HttpStatus.BAD_REQUEST);
        }
        if(authentication.isAuthenticated()){
            log.info("Call start generateToken method for user {}",requestDto.getUserName());
            ServiceExceptionHandler<String> dataHandler = () -> authService.generateToken(requestDto.getUserName());
            String token = dataHandler.executeHandler();
            return buildSuccessResponse(GET_TOKEN_VALID_MESSAGE,new UserInfoResponseDto(token));
        }else {
            log.error("User/Password is Invalid");
            return buildErrorResponse(GET_USER_FAILED_MESSAGE, HttpStatus.BAD_REQUEST);
        }
    }

    // Validate Token
    @GetMapping(value = API_POST_VALIDATE)
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> validateToken(@RequestParam("token") String token) {
        ServiceExceptionHandler<Boolean> dataHandler = () -> authService.validateToken(token);
        if(!dataHandler.executeHandler()){
            log.error("Invalid Token Found");
            return buildErrorResponse(GET_TOKEN_INVALID_MESSAGE, HttpStatus.NOT_FOUND);
        }
        return buildSuccessResponse(GET_TOKEN_VALID_MESSAGE,null);
    }

    private ResponseEntity<ApiResponse<UserInfoResponseDto>> buildSuccessResponse(String message, UserInfoResponseDto data) {
        ApiResponse<UserInfoResponseDto> response = new ApiResponse<>(SUCCESS_STATUS, message, data, null);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<ApiResponse<UserInfoResponseDto>> buildErrorResponse(String message, HttpStatus status) {
        ApiResponse<UserInfoResponseDto> response = new ApiResponse<>(FAILED_STATUS, message, null, null);
        return ResponseEntity.status(status).body(response);
    }

}
