package com.example.hogwarts_artifacts_online.hogwartsUser;

import com.example.hogwarts_artifacts_online.hogwartsUser.dto.UserDto;
import com.example.hogwarts_artifacts_online.system.StatusCode;
import com.example.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @Value("${api.endpoint.base-url}/users")
    String baseUrl;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllUsersSuccess() throws Exception {
        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1L);
        u1.setUsername("john");
        u1.setPassword("123456");
        u1.setEnabled(true);
        u1.setRoles("admin user");

        HogwartsUser u2 = new HogwartsUser();
        u2.setId(2L);
        u2.setUsername("eric");
        u2.setPassword("654321");
        u2.setEnabled(true);
        u2.setRoles("user");

        HogwartsUser u3 = new HogwartsUser();
        u3.setId(3L);
        u3.setUsername("tom");
        u3.setPassword("qwerty");
        u3.setEnabled(false);
        u3.setRoles("user");

        List<HogwartsUser> users = List.of(u1, u2, u3);

        given(this.userService.findAll()).willReturn(users);

        mockMvc.perform(get(this.baseUrl).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data.size()").value(users.size()))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].username").value("john"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].username").value("eric"));
    }

    @Test
    void testFindUserByIdSuccess() throws Exception {
        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1L);
        u1.setUsername("john");
        u1.setPassword("123456");
        u1.setEnabled(true);
        u1.setRoles("admin user");

        given(this.userService.findById(1l)).willReturn(u1);

        mockMvc.perform(get(this.baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(u1.getId()))
                .andExpect(jsonPath("$.data.username").value(u1.getUsername()))
                .andExpect(jsonPath("$.data.roles").value(u1.getRoles()));


    }
    @Test
    void testFindUserByIdNotFound() throws Exception {
        given(this.userService.findById(1l)).willThrow(new ObjectNotFoundException("user", "1"));

        mockMvc.perform(get(this.baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAddUserSuccess() throws Exception {
        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1L);
        u1.setUsername("john");
        u1.setPassword("123456");
        u1.setEnabled(true);
        u1.setRoles("admin user");

        String json = objectMapper.writeValueAsString(u1);

        HogwartsUser savedUser = new HogwartsUser();
        savedUser.setId(1L);
        savedUser.setUsername("john");
        savedUser.setEnabled(true);
        savedUser.setRoles("admin user");

        given(this.userService.save(Mockito.any(HogwartsUser.class))).willReturn(savedUser);

        mockMvc.perform(post(this.baseUrl).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.data.username").value(savedUser.getUsername()))
                .andExpect(jsonPath("$.data.enabled").value(savedUser.getEnabled()))
                .andExpect(jsonPath("$.data.roles").value(savedUser.getRoles()));
    }

    @Test
    void testUpdateUserSuccess() throws Exception{
        UserDto userDto = new UserDto(null, "john-updated", true, "admin user");

        String json = objectMapper.writeValueAsString(userDto);

        HogwartsUser updated = new HogwartsUser();
        updated.setId(1L);
        updated.setUsername("john-updated");
        updated.setEnabled(true);
        updated.setRoles("admin user");

        given(this.userService.update(Mockito.anyLong(), Mockito.any(HogwartsUser.class)))
                .willReturn(updated);

        mockMvc.perform(put(this.baseUrl + "/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(updated.getId()))
                .andExpect(jsonPath("$.data.username").value(updated.getUsername()))
                .andExpect(jsonPath("$.data.enabled").value(updated.getEnabled()))
                .andExpect(jsonPath("$.data.roles").value(updated.getRoles()));
    }

    @Test
    void testUpdateUserErrorWithNonExistenceId() throws Exception{
        UserDto userDto = new UserDto(null, "john-updated", true, "admin user");

        String json = objectMapper.writeValueAsString(userDto);

        given(this.userService.update(Mockito.anyLong(), Mockito.any(HogwartsUser.class)))
                .willThrow(new ObjectNotFoundException("user", "1"));

        mockMvc.perform(put(this.baseUrl + "/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        doNothing().when(this.userService).delete(1l);

        mockMvc.perform(delete(this.baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteUserErrorWithNonExistenceID() throws Exception {
        doThrow(new ObjectNotFoundException("user", "1")).when(this.userService).delete(1l);

        mockMvc.perform(delete(this.baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}