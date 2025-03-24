package com.example.hogwarts_artifacts_online.security;


import com.example.hogwarts_artifacts_online.hogwartsUser.HogwartsUser;
import com.example.hogwarts_artifacts_online.hogwartsUser.MyUserPrincipal;
import com.example.hogwarts_artifacts_online.hogwartsUser.converter.UserToUserDtoConverter;
import com.example.hogwarts_artifacts_online.hogwartsUser.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final JwtProvider jwtProvider;

    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JwtProvider jwtProvider,UserToUserDtoConverter userToUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }


    public Map<String, Object> createLoginInfo(Authentication authentication) {
        //userInfo
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDto userDto = this.userToUserDtoConverter.convert(hogwartsUser);
        //token
        String token = this.jwtProvider.createToken(authentication);

        Map<String, Object> loginResultMap = new HashMap<>();
        loginResultMap.put("UserInfo", userDto);
        loginResultMap.put("Token", token);
        return loginResultMap;
    }
}
