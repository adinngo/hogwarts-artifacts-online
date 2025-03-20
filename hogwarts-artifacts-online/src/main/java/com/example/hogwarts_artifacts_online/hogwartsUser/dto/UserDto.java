package com.example.hogwarts_artifacts_online.hogwartsUser.dto;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Component;


public record UserDto(Long id,
                      @NotEmpty(message = "name is required")
                      String username,
                      boolean enabled,
                      @NotEmpty(message = "roles are required")
                      String roles) {

}
