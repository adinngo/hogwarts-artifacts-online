package com.example.hogwarts_artifacts_online.artifact;

import com.example.hogwarts_artifacts_online.wizard.Wizard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @InjectMocks
    ArtifactService artifactService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        //given: Arrange inputs and target. Define a behavior of mock object ArtifactRepository
        Artifact a = new Artifact();
        a.setId("1250808601744904192");
        a.setName("Invisibility Cloak");
        a.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a.setImageUrl("ImageUrl");

        Wizard w = new Wizard();
        w.setId("2");
        w.setName("Harry Potter");

        a.setOwner(w);

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));
        //when
        Artifact returnedArtifact = artifactService.findById("1250808601744904192");
        //then
        assertThat(returnedArtifact.getId()).isEqualTo(a.getId());
        assertThat(returnedArtifact.getName()).isEqualTo(a.getName());
        assertThat(returnedArtifact.getDescription()).isEqualTo(a.getDescription());
        assertThat(returnedArtifact.getImageUrl()).isEqualTo(a.getImageUrl());
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void testFindByIdNotFound() {
        //given
        given(artifactRepository.findById(Mockito.anyString())).willReturn(Optional.empty());
        //when
        Throwable thrown = catchThrowable(() -> {
            Artifact returnedArtifact = artifactService.findById("1250808601744904192");
        });
        //then
        assertThat(thrown).isInstanceOf(ArtifactNotFoundException.class).hasMessage("Could not found artifact with Id 1250808601744904192");
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }
}