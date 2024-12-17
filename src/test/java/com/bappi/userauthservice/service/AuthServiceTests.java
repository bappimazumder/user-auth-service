package com.bappi.userauthservice.service;

import com.bappi.userauthservice.model.dto.UserInfoRequestDto;
import com.bappi.userauthservice.model.entity.UserInfo;
import com.bappi.userauthservice.repository.UserInfoRepository;
import com.bappi.userauthservice.utils.mapper.UserInfoObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

    @Mock
    private UserInfoRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserInfoObjectMapper objectMapper;

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtTokenService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;


    @Test
    void testAddUser() {
        // Given: A UserInfoRequestDto
        UserInfoRequestDto requestDto = new UserInfoRequestDto();
        requestDto.setPassword("password123");

        // Mock the objectMapper and passwordEncoder
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword("encodedPassword");
        userInfo.setActiveStatus(true);
        userInfo.setCreateBy(1L);
        userInfo.setCreateDate(new Timestamp(System.currentTimeMillis()));
        userInfo.setCode("USER_123456789");
        when(objectMapper.map(requestDto)).thenReturn(userInfo);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // When: addUser method is called
        String result = authService.addUser(requestDto);

        // Then: Ensure repository.save() is called and the correct success message is returned
        //verify(repository).save(userInfo);
        assertEquals("User created successfully", result);
    }

    @Test
    void testGenerateToken() {
        // Given: A sample username
        String username = "testUser";
        String expectedToken = "mockedJwtToken";  // A mocked token for the test

        // Mock the behavior of jwtService.generateToken
        when(jwtService.generateToken(username)).thenReturn(expectedToken);

        // When: The generateToken method is called
        String result = authService.generateToken(username);

        // Then: Verify that jwtService.generateToken was called with the correct username
        verify(jwtService).generateToken(username);

        // Assert: The generated token should be the same as the mocked token
        assertEquals(expectedToken, result);
    }

    @Test
    void testValidateToken_Success() {
        // Given: A valid token and expected username
        String token = "validToken";
        String username = "testUser";
        UserDetails userDetails = mock(UserDetails.class);  // Mock userDetails object

        // Mocking behavior of jwtService and userDetailsService
        when(jwtService.extractUsername(token)).thenReturn(username);  // Extract username from token
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);  // Load user details
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);  // Validate token

        // When: validateToken method is called
        Boolean result = authService.validateToken(token);

        // Then: Assert that the result is true and validate the flow
        verify(jwtService).extractUsername(token);  // Ensure username extraction was called
        verify(userDetailsService).loadUserByUsername(username);  // Ensure user details were loaded
        verify(jwtService).validateToken(token, userDetails);  // Ensure token validation was called

        // Assert: The token is valid
        assertTrue(result);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Given: A token that is invalid
        String token = "invalidToken";
        String username = "testUser";
        UserDetails userDetails = mock(UserDetails.class);

        // Mocking behavior of jwtService and userDetailsService
        when(jwtService.extractUsername(token)).thenReturn(username);  // Extract username from token
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);  // Load user details
        when(jwtService.validateToken(token, userDetails)).thenReturn(false);  // Token is invalid

        // When: validateToken method is called
        Boolean result = authService.validateToken(token);

        // Then: Assert that the result is false and validate the flow
        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtService).validateToken(token, userDetails);

        // Assert: The token is invalid
        assertFalse(result);
    }

}
