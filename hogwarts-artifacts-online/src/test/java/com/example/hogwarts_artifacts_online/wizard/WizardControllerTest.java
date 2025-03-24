package com.example.hogwarts_artifacts_online.wizard;

import com.example.hogwarts_artifacts_online.system.StatusCode;
import com.example.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import com.example.hogwarts_artifacts_online.wizard.dto.WizardDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class WizardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    WizardService wizardService;

    @Value("${api.endpoint.base-url}/wizards")
    String baseUrl;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindWizardById() throws Exception {
        Wizard w1 = new Wizard();
        w1.setId(1L);
        w1.setName("Albus Dumbledore");
        given(wizardService.findById("1")).willReturn(w1);

        //when and then
        mockMvc.perform(get(this.baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(w1.getId()))
                .andExpect(jsonPath("$.data.name").value(w1.getName()));


    }

    @Test
    void testFindWizardByIdNotFound() throws Exception {
        given(wizardService.findById("1")).willThrow(new ObjectNotFoundException("Wizard","1"));

        //when and then
        mockMvc.perform(get(this.baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Wizard with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testFindAllWizard() throws Exception {
        Wizard w1 = new Wizard();
        w1.setId(1L);
        w1.setName("Albus Dumbledore");

        Wizard w2 = new Wizard();
        w2.setId(2L);
        w2.setName("Harry Potter");

        Wizard w3 = new Wizard();
        w3.setId(3L);
        w3.setName("Neville Longbottom");

        List<Wizard> wizardList = List.of(w1, w2, w3);

        given(this.wizardService.findAll()).willReturn(wizardList);

        //when and then
        mockMvc.perform(get(this.baseUrl).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data[0].id").value(wizardList.get(0).getId()))
                .andExpect(jsonPath("$.data[0].name").value(wizardList.get(0).getName()))
                .andExpect(jsonPath("$.data[1].id").value(wizardList.get(1).getId()))
                .andExpect(jsonPath("$.data[1].name").value(wizardList.get(1).getName()));
    }

    @Test
    void testAddWizardSuccess() throws Exception {
        WizardDto wizardDto = new WizardDto(null, "Hermione Granger", null);
        String json = objectMapper.writeValueAsString(wizardDto);

        Wizard updatedWizard = new Wizard();
        updatedWizard.setId(1L);
        updatedWizard.setName("Hermione Granger");

        given(this.wizardService.save(Mockito.any(Wizard.class))).willReturn(updatedWizard);

        mockMvc.perform(post(this.baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.data.id").value(updatedWizard.getId()))
                .andExpect(jsonPath("$.data.name").value(updatedWizard.getName()));
    }

    @Test
    void testUpdateWizardSuccess() throws Exception {
        WizardDto wizardDto = new WizardDto(null, "Hermione Granger", null);
        String json = objectMapper.writeValueAsString(wizardDto);

        Wizard updatedWizard = new Wizard();
        updatedWizard.setId(1L);
        updatedWizard.setName("Hermione Granger");

        given(this.wizardService.update(Mockito.anyString(), Mockito.any(Wizard.class))).willReturn(updatedWizard);

        mockMvc.perform(put(this.baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(updatedWizard.getId()))
                .andExpect(jsonPath("$.data.name").value(updatedWizard.getName()));
    }

    @Test
    void testUpdateWizardErrorWithNonExistenceId() throws Exception{
        WizardDto wizardDto = new WizardDto(null, "Hermione Granger", null);
        String json = objectMapper.writeValueAsString(wizardDto);

        Wizard updatedWizard = new Wizard();
        updatedWizard.setId(1L);
        updatedWizard.setName("Hermione Granger");

        given(this.wizardService.update(Mockito.anyString(), Mockito.any(Wizard.class)))
                .willThrow(new ObjectNotFoundException("Wizard","1"));

        mockMvc.perform(put(this.baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Wizard with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteWizardSuccess() throws Exception{
        doNothing().when(this.wizardService).delete(Mockito.anyString());

        mockMvc.perform(delete(this.baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testDeleteWizardErrorWithNonExistenceId() throws Exception{
        doThrow(new ObjectNotFoundException("Wizard","1")).when(this.wizardService).delete(Mockito.anyString());

        mockMvc.perform(delete(this.baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Wizard with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testAssignArtifactSuccess() throws Exception {
        doNothing().when(this.wizardService).assignArtifact(Mockito.anyString(), Mockito.anyString());
        mockMvc.perform(put(this.baseUrl + "/1/artifacts/1250808601744904196").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Artifact Assignment Success"))
                .andExpect(jsonPath("$.data").isEmpty());


    }

    @Test
    void testAssignArtifactErrorWithNonExistenceWizardId() throws Exception {
        doThrow(new ObjectNotFoundException("Wizard", "1"))
                .when(this.wizardService)
                .assignArtifact(Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(put(this.baseUrl + "/1/artifacts/1250808601744904196").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Wizard with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());


    }

    @Test
    void testAssignArtifactErrorWithNonExistenceArtifactId() throws Exception {
        doThrow(new ObjectNotFoundException("Artifact", "1250808601744904196"))
                .when(this.wizardService)
                .assignArtifact(Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(put(this.baseUrl + "/1/artifacts/1250808601744904196").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Artifact with Id 1250808601744904196"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}