package com.borisov.DZ_4.controllers;

import com.borisov.DZ_4.dto.UserCreateDTO;
import com.borisov.DZ_4.dto.UserResponseDTO;
import com.borisov.DZ_4.service.UserService;
import com.borisov.DZ_4.util.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsersController.class)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    public static final OffsetDateTime TIMESTAMP_1 = OffsetDateTime.parse("2000-01-01T00:00:00Z");
    public static final OffsetDateTime TIMESTAMP_2 = OffsetDateTime.parse("2001-01-01T00:00:00Z");


    @Test
    @DisplayName("GET /users should return list of users")
    void getUsersShouldReturnList() throws Exception {
        // Arrange
        UserResponseDTO dto1 = new UserResponseDTO(15, "Alice", "alice@example.com", 11, TIMESTAMP_1);
        UserResponseDTO dto2 = new UserResponseDTO(18, "Bob", "bob@example.com", 22, TIMESTAMP_2);
        when(userService.findAll()).thenReturn(Arrays.asList(dto1, dto2));
        // Act & Assert
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(15))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].id").value(18))
                .andExpect(jsonPath("$[1].name").value("Bob"));
    }

    @Test
    @DisplayName("GET /users should return blank list")
    void getUsersShouldReturnBlankList() throws Exception {
        when(userService.findAll()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));

    }



    @Test
    @DisplayName("GET /users/{id} should return user when found")
    void getUserByIdShouldReturnUser() throws Exception {
        int test_id = 33;
        UserResponseDTO dto = new UserResponseDTO(test_id, "Alice", "alice@example.com", 11, TIMESTAMP_1);
        when(userService.findById(test_id)).thenReturn(dto);

        mockMvc.perform(get("/users/{id}", test_id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(test_id))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    @DisplayName("GET /users/{id} should return error 404 when user not found")
    void getUserByIdShouldReturnError404() throws Exception {
        int test_id = 33;
        when(userService.findById(test_id)).thenThrow(new UserNotFoundException("id",String.valueOf(test_id)));

        mockMvc.perform(get("/users/{id}", test_id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Не удалось найти пользователя с id="+test_id))
                .andExpect(jsonPath("$.timestamp").exists());
    }




    @Test
    @DisplayName("DELETE /users/{id} should delete user and return 204")
    void deleteUserShouldDeleteUserAndReturnStatus204() throws Exception {
        int test_id = 33;
        doNothing().when(userService).deleteById(test_id);

        mockMvc.perform(delete("/users/{id}", test_id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /users/{id} should return error 404 when user not found")
    void deleteUserShouldReturnError404() throws Exception {
        int test_id = 33;
        doThrow(new UserNotFoundException("id",String.valueOf(test_id))).when(userService).deleteById(test_id);

        mockMvc.perform(delete("/users/{id}", test_id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Не удалось найти пользователя с id="+test_id))
                .andExpect(jsonPath("$.timestamp").exists());
    }




    @Test
    @DisplayName("POST /users should create user and return 201 with Location header")
    void createUserAndReturn201withLocationHeader() throws Exception {
        int test_id = 14;
        String json = "{\"name\":\"Bob\",\"email\":\"bob@example.com\",\"age\":22}";
        UserCreateDTO user = new UserCreateDTO( "Bob", "bob@example.com", 22);

        when(userService.existsByEmail(anyString(), any(Integer.class))).thenReturn(false);
        when(userService.save(any(UserCreateDTO.class))).thenReturn(test_id);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/" + test_id));
    }

    @Test
    @DisplayName("POST /users return Error 400 - age not be less 0")
    void testCreateReturn400AgeInvalid() throws Exception {
        String json = "{\"name\":\"Bob\",\"email\":\"bob@example.com\",\"age\":-22}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "ru")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("age - должно быть не меньше 0;"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /users return Error 400 - age not be empty")
    void testCreateReturn400AgeNotNull() throws Exception {
        String json = "{\"name\":\"Bob\",\"email\":\"bob@example.com\",\"age\":null}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "ru")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("age - не должно равняться null;"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /users return Error 400 - name not be empty")
    void testCreateReturn400NameNotEmpty() throws Exception {
        String json = "{\"name1\":\"Bob\",\"email\":\"bob@example.com\",\"age\":10}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "ru")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("name - не должно быть пустым;"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /users return Error 409 - email already exist")
    void testCreateReturn409EmailAlreadyExist() throws Exception {
        String json = "{\"name\":\"Bob\",\"email\":\"bob@example.com\",\"age\":10}";
        when(userService.existsByEmail(anyString(), eq(null))).thenReturn(true);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email уже используется"))
                .andExpect(jsonPath("$.timestamp").exists());
    }




    @Test
    @DisplayName("PATCH /users/{id} should update user and return DTO")
    void updateUserReturnUpdatedUser() throws Exception {
        int test_id = 11;
        String json = "{\"name\":\"AliceUpdated\",\"email\":\"alice@example.com\",\"age\":28}";
        UserResponseDTO userOut = new UserResponseDTO(test_id, "AliceUpdated", "alice@example.com", 28, TIMESTAMP_1);

        when(userService.existsByEmail(anyString(), any(Integer.class))).thenReturn(false);
        when(userService.updateById(eq(test_id), any(UserCreateDTO.class))).thenReturn(userOut);

        mockMvc.perform(patch("/users/{id}", test_id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(test_id))
                .andExpect(jsonPath("$.name").value("AliceUpdated"));
    }

    @Test
    @DisplayName("PATCH /users/{id} return Error 409 - email already exist")
    void updateUserReturn409EmailAlreadyExist() throws Exception {
        int test_id = 14;
        String json = "{\"name\":\"Bob\",\"email\":\"bob@example.com\",\"age\":10}";
        when(userService.existsByEmail(anyString(), anyInt())).thenReturn(true);

        mockMvc.perform(patch("/users/{id}", test_id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email уже используется"))
                .andExpect(jsonPath("$.timestamp").exists());

    }

    @Test
    @DisplayName("PATCH /users/{id} error 404 when user not found")
    void updateUserShouldReturnError404() throws Exception {
        int test_id = 11;
        String json = "{\"name\":\"AliceUpdated\",\"email\":\"alice@example.com\",\"age\":28}";

        when(userService.existsByEmail(anyString(), any(Integer.class))).thenReturn(false);
        when(userService.updateById(anyInt(), any(UserCreateDTO.class))).thenThrow(new UserNotFoundException("id",String.valueOf(test_id)));

        mockMvc.perform(patch("/users/{id}", test_id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Не удалось найти пользователя с id="+test_id))
                .andExpect(jsonPath("$.timestamp").exists());
    }


}
