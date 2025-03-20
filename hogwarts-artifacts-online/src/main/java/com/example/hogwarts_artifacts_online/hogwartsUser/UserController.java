package com.example.hogwarts_artifacts_online.hogwartsUser;

import com.example.hogwarts_artifacts_online.hogwartsUser.converter.UserDtoToUserConverter;
import com.example.hogwarts_artifacts_online.hogwartsUser.converter.UserToUserDtoConverter;
import com.example.hogwarts_artifacts_online.hogwartsUser.dto.UserDto;
import com.example.hogwarts_artifacts_online.system.Result;
import com.example.hogwarts_artifacts_online.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {

    private final UserService userService;

    private final UserToUserDtoConverter userToUserDtoConverter;

    private final UserDtoToUserConverter userDtoToUserConverter;


    public UserController(UserService userService,
                          UserToUserDtoConverter userToUserDtoConverter,
                          UserDtoToUserConverter userDtoToUserConverter) {
        this.userService = userService;
        this.userToUserDtoConverter = userToUserDtoConverter;
        this.userDtoToUserConverter = userDtoToUserConverter;
    }

    @GetMapping
    public Result findAllUsers() {
        List<UserDto> usersDto = this.userService.findAll().stream().
                map(this.userToUserDtoConverter::convert)
                .collect(Collectors.toList());
        return new Result(true, StatusCode.SUCCESS, "Find All Success", usersDto);
    }

    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable Long userId) {
        HogwartsUser returnedUser = this.userService.findById(userId);
        var returnedUserDto = this.userToUserDtoConverter.convert(returnedUser);

        return new Result(true, StatusCode.SUCCESS, "Find One Success", returnedUserDto);
    }

    //we don't user userDTO because password is required
    @PostMapping
    public Result addUser(@Valid @RequestBody HogwartsUser newUser) {
        HogwartsUser savedUser = this.userService.save(newUser);
        UserDto savedUserDto = this.userToUserDtoConverter.convert(savedUser);
        return new Result(true, StatusCode.SUCCESS, "Add Success", savedUserDto);
    }

    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable Long userId,
                             @Valid @RequestBody UserDto userDto) {
        HogwartsUser user = this.userDtoToUserConverter.convert(userDto);
        HogwartsUser updatedUser = this.userService.update(userId, user);
        UserDto updatedUserDto = this.userToUserDtoConverter.convert(updatedUser);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable Long userId) {
        this.userService.delete(userId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success", null);
    }
}
