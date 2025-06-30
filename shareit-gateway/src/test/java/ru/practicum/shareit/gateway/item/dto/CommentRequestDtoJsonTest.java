package ru.practicum.shareit.gateway.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentRequestDtoJsonTest {

    @Autowired
    private JacksonTester<CommentRequestDto> json;

    @Test
    void testCommentRequestDto_Serialize() throws IOException {
        // Подготавливаем объект
        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("This is a great item!");

        // Сериализуем в JSON
        JsonContent<CommentRequestDto> result = json.write(dto);

        // Проверяем JSON
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("This is a great item!");
    }

    @Test
    void testCommentRequestDto_Deserialize() throws IOException {
        // Подготавливаем JSON
        String content = """
                    {
                      "text": "Very good!"
                    }
                """;

        // Десериализуем в объект
        CommentRequestDto dto = json.parseObject(content);

        // Проверяем поля
        assertThat(dto.getText()).isEqualTo("Very good!");
    }
}