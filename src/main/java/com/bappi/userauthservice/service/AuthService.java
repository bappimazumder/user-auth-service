package com.bappi.userauthservice.service;

import com.bappi.userauthservice.model.dto.UserInfoRequestDto;
import com.bappi.userauthservice.model.entity.UserInfo;
import com.bappi.userauthservice.repository.UserInfoRepository;
import com.bappi.userauthservice.utils.mapper.UserInfoObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import static com.bappi.userauthservice.config.Constant.CREATED_SUCCESSFULLY_MESSAGE;
import static com.bappi.userauthservice.config.Constant.USER_CODE_PREFIX;

@Slf4j
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserInfoObjectMapper objectMapper;

    private final UserInfoRepository repository;

    private final JwtTokenService jwtService;

    private final UserDetailsServiceImpl userDetailsService;

    public AuthService(PasswordEncoder passwordEncoder, UserInfoRepository repository, JwtTokenService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = Mappers.getMapper(UserInfoObjectMapper.class);
    }

    // Register New user
    public String addUser(UserInfoRequestDto requestDto){

        int uniqueId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        String userCode = USER_CODE_PREFIX + uniqueId;

        UserInfo userInfo = objectMapper.map(requestDto);
        userInfo.setActiveStatus(true);
        userInfo.setCode(userCode);
        userInfo.setCreateBy(1L);
        userInfo.setCreateDate(new Timestamp(System.currentTimeMillis()));
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return CREATED_SUCCESSFULLY_MESSAGE;
    }

    // Generate Token
    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    // Token Validation
    public Boolean validateToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            if(username != null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                return jwtService.validateToken(token,userDetails);
            }
        }catch (Exception e){
            log.error("Error validating token {}", e.getMessage());
            return false;
        }
        return false;

    }

    public Optional<UserInfo> getByUserName(String userName) {
        return repository.findByUserName(userName);
    }

    public Optional<UserInfo> getByEmail(String email) {
        return repository.findByEmail(email);
    }


}
