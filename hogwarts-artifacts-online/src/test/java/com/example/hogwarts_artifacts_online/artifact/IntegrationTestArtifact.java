package com.example.hogwarts_artifacts_online.artifact;


import com.example.hogwarts_artifacts_online.artifact.dto.ArtifactDto;
import com.example.hogwarts_artifacts_online.system.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Integration tests for artifact")
public class IntegrationTestArtifact {

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
    void testFindAllArtifactSuccess() throws Exception {
        mockMvc.perform(get(baseUrl + "/artifacts").accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(6)));
    }



    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)// reset h2 database before test
    void testFindArtifactByIdSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/artifacts/1250808601744904191")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data.name").value("Deluminator"));
    }

    @Test
    void testFindArtifactByIdNotFound() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/artifacts/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Artifact with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddArtifactSuccess() throws Exception {
        ArtifactDto artifactDto = new ArtifactDto(null,
                "Update name", "Update description", "Update images", null);

        String json = objectMapper.writeValueAsString(artifactDto);

        this.mockMvc.perform(post(this.baseUrl + "/artifacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("Update name"))
                .andExpect(jsonPath("$.data.description").value("Update description"))
                .andExpect(jsonPath("$.data.imageUrl").value("Update images"))
        ;

        mockMvc.perform(get(baseUrl + "/artifacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(7)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)// reset h2 database before test
    void testUpdateArtifactSuccess() throws Exception {
        ArtifactDto artifactDto = new ArtifactDto(null,
                "Update name", "Update description", "Update images", null);

        String json = objectMapper.writeValueAsString(artifactDto);

        this.mockMvc.perform(put(this.baseUrl + "/artifacts/1250808601744904191")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("Update name"))
                .andExpect(jsonPath("$.data.description").value("Update description"))
                .andExpect(jsonPath("$.data.imageUrl").value("Update images"))
        ;

        this.mockMvc.perform(get(this.baseUrl + "/artifacts/1250808601744904191")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data.name").value("Update name"))
                .andExpect(jsonPath("$.data.description").value("Update description"))
                .andExpect(jsonPath("$.data.imageUrl").value("Update images"));
    }

    @Test
    void testUpdateArtifactErrorWithNonExistence() throws Exception {
        ArtifactDto artifactDto = new ArtifactDto(null,
                "Update name", "Update description", "Update images", null);

        String json = objectMapper.writeValueAsString(artifactDto);

        this.mockMvc.perform(put(this.baseUrl + "/artifacts/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Artifact with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testDeleteArtifactSuccess() throws Exception {
        this.mockMvc.perform(delete(this.baseUrl + "/artifacts/1250808601744904191")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());

        mockMvc.perform(get(baseUrl + "/artifacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(5)));
    }

    @Test
    void testDeleteArtifactErrorWithNonExistentId() throws Exception {
        this.mockMvc.perform(delete(this.baseUrl + "/artifacts/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Artifact with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
