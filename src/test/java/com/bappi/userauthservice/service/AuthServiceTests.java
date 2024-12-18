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
        UserInfoRequestDto requestDto = new UserInfoRequestDto();
        requestDto.setPassword("password123");

        UserInfo userInfo = new UserInfo();
        userInfo.setPassword("password");
        userInfo.setActiveStatus(true);
        userInfo.setCreateBy(1L);
        userInfo.setCreateDate(new Timestamp(System.currentTimeMillis()));
        userInfo.setCode("USER_123456");
        when(objectMapper.map(requestDto)).thenReturn(userInfo);
        when(passwordEncoder.encode("password")).thenReturn("password123");


        String result = authService.addUser(requestDto);

        assertEquals("User created successfully", result);
    }

    @Test
    void testGenerateToken() {

        String username = "testUser";
        String expectedToken = "mockedJwtToken";

        when(jwtService.generateToken(username)).thenReturn(expectedToken);

        String result = authService.generateToken(username);

        verify(jwtService).generateToken(username);

        assertEquals(expectedToken, result);
    }

    @Test
    void testValidateToken_Success() {

        String token = "validToken";
        String username = "testUser";
        UserDetails userDetails = mock(UserDetails.class);

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);

        Boolean result = authService.validateToken(token);

        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtService).validateToken(token, userDetails);

        assertTrue(result);
    }

    @Test
    void testValidateToken_InvalidToken() {

        String token = "invalidToken";
        String username = "testUser";
        UserDetails userDetails = mock(UserDetails.class);

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(false);

        Boolean result = authService.validateToken(token);

        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtService).validateToken(token, userDetails);

        assertFalse(result);
    }

}
