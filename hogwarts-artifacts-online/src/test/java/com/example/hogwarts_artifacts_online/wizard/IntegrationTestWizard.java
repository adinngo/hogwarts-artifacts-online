package com.example.hogwarts_artifacts_online.wizard;


import com.example.hogwarts_artifacts_online.system.StatusCode;
import com.example.hogwarts_artifacts_online.wizard.dto.WizardDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTestWizard {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    String token;

    @BeforeEach
    void setUp() throws Exception {
//        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + "/auth/login").header(HttpHeaders.AUTHORIZATION,
//                "Basic " + Base64Utils.encodeToString("john:123456".getBytes())));
        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + "/users/login")
                .with(httpBasic("john", "123456"))); // httpBasic() is from spring-security-test.
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        this.token = "Bearer " + json.getJSONObject("data").getString("Token"); // Don't forget to add "Bearer " as prefix.
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)// reset h2 database before test
    void testFindAllWizardSuccess() throws Exception {
        mockMvc.perform(get(baseUrl + "/wizards").accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)));
    }



    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)// reset h2 database before test
    void testFindWizardByIdSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/wizards/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.name").value("Albus Dumbledore"));
    }

    @Test
    void testFindWizardByIdNotFound() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/wizards/4")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Wizard with Id 4"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddWizardSuccess() throws Exception {
        WizardDto wizardDto = new WizardDto(null, "Adin", null);

        String json = objectMapper.writeValueAsString(wizardDto);

        this.mockMvc.perform(post(this.baseUrl + "/wizards")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("Adin"));

        mockMvc.perform(get(baseUrl + "/wizards")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
    }

    @Test
    void testUpdateWizardsSuccess() throws Exception {
        WizardDto wizardDto = new WizardDto(null, "Updated name", null);

        String json = objectMapper.writeValueAsString(wizardDto);

        this.mockMvc.perform(put(this.baseUrl + "/wizards/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("Updated name"));
    }

    @Test
    void testUpdateWizardsErrorWithNonExistenceId() throws Exception {
        WizardDto wizardDto = new WizardDto(null, "Updated name", null);

        String json = objectMapper.writeValueAsString(wizardDto);

        this.mockMvc.perform(put(this.baseUrl + "/wizards/4")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Wizard with Id 4"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testDeleteWizardSuccess() throws Exception {
        this.mockMvc.perform(delete(this.baseUrl + "/wizards/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteWizardErrorWithNonExistenceId() throws Exception {
        this.mockMvc.perform(delete(this.baseUrl + "/wizards/4")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Wizard with Id 4"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssignArtifactSuccess() throws Exception {
        this.mockMvc.perform(put(this.baseUrl + "/wizards/1/artifacts/1250808601744904196")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Artifact Assignment Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssignArtifactErrorWithNonExistenceWizardsId() throws Exception {
        this.mockMvc.perform(put(this.baseUrl + "/wizards/4/artifacts/1250808601744904196")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Wizard with Id 4"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssignArtifactErrorWithNonExistenceAritfactId() throws Exception {
        this.mockMvc.perform(put(this.baseUrl + "/wizards/1/artifacts/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Artifact with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}
