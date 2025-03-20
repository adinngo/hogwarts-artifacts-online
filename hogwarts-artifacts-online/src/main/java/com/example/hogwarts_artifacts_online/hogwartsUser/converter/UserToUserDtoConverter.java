package com.example.hogwarts_artifacts_online.hogwartsUser.converter;

import com.example.hogwarts_artifacts_online.hogwartsUser.HogwartsUser;
import com.example.hogwarts_artifacts_online.hogwartsUser.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements Converter<HogwartsUser, UserDto> {

    @Override
    public UserDto convert(HogwartsUser source) {
        UserDto userDto = new UserDto(source.getId(),
                source.getUsername(),
                source.getEnabled(),
                source.getRoles());
        return userDto;
    }
}
