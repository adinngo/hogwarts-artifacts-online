package com.example.hogwarts_artifacts_online.wizard.dto;

import jakarta.validation.constraints.NotEmpty;

public record WizardDto(Long id,
                        @NotEmpty(message = "name is required")
                        String name,
                        Integer NumberOfArtifacts) {
}
