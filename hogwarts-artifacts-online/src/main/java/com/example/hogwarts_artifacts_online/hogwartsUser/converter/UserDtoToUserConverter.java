package com.example.hogwarts_artifacts_online.hogwartsUser.converter;

import com.example.hogwarts_artifacts_online.hogwartsUser.HogwartsUser;
import com.example.hogwarts_artifacts_online.hogwartsUser.dto.UserDto;
import org.apache.catalina.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, HogwartsUser> {
    @Override
    public HogwartsUser convert(UserDto source) {
        HogwartsUser user = new HogwartsUser();
        user.setId(source.id());
        user.setUsername(source.username());
        user.setEnabled(source.enabled());
        user.setRoles(source.roles());
        return user;
    }
}
