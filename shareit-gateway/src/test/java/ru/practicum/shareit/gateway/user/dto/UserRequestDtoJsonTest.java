package ru.practicum.shareit.gateway.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserRequestDtoJsonTest {

    @Autowired
    private JacksonTester<UserRequestDto> json;

    @Test
    void testUserRequestDto_Serialize() throws IOException {
        // Подготавливаем объект
        UserRequestDto dto = new UserRequestDto();
        dto.setName("John");
        dto.setEmail("john.doe@example.com");

        // Сериализуем в JSON
        var jsonContent = json.write(dto);

        // Проверяем содержимое JSON
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("John");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@example.com");
    }

    @Test
    void testUserRequestDto_Deserialize() throws IOException {
        // Подготавливаем JSON
        String content = "{ \"name\": \"Alice\", \"email\": \"alice@example.com\" }";

        // Десериализуем в объект
        UserRequestDto dto = json.parseObject(content);

        // Проверяем поля
        assertThat(dto.getName()).isEqualTo("Alice");
        assertThat(dto.getEmail()).isEqualTo("alice@example.com");
    }
}