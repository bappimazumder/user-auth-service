package com.bappi.userauthservice.utils.mapper;

import com.bappi.userauthservice.model.dto.UserInfoRequestDto;
import com.bappi.userauthservice.model.entity.UserInfo;
import org.mapstruct.Mapper;

@Mapper
public interface UserInfoObjectMapper {
    UserInfo map(UserInfoRequestDto requestDto);
}
