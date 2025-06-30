package ru.practicum.shareit.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.server.user.dto.UserRequestDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;
import ru.practicum.shareit.server.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.server.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
@Import(UserControllerTest.TestConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private UserResponseDto savedUser;


    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        savedUser = new UserResponseDto();
        savedUser.setId(1L);
        savedUser.setName("John Doe");
        savedUser.setEmail("john.doe@example.com");
    }

    @Test
    void createUser_ReturnsCreatedUser() throws Exception {
        UserRequestDto newUser = new UserRequestDto();
        newUser.setName("John");
        newUser.setEmail("john@example.com");

        when(userService.createUser(any(UserRequestDto.class)))
                .thenReturn(savedUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value(savedUser.getName()))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()));
    }

    @Test
    void getUserById_ReturnsUser() throws Exception {
        long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(savedUser);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value(savedUser.getName()))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()));
    }

    @Test
    void updateUser_ReturnsUpdatedUser() throws Exception {
        long userId = 1L;
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");

        UserResponseDto updatedUser = new UserResponseDto();
        updatedUser.setId(userId);
        updatedUser.setName(updateDto.getName());
        updatedUser.setEmail(updateDto.getEmail());

        when(userService.updateUser(any(), eq(userId))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(updateDto.getName()))
                .andExpect(jsonPath("$.email").value(updateDto.getEmail()));
    }

    @Test
    void deleteUser_ReturnsNoContent() throws Exception {
        long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }


}
