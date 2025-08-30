package ru.practicum.shareit.exceptions;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
class ErrorHandlerTest {
    @Autowired
    MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test-exception")
        public void throwException() throws Exception {
            throw new Exception("Внутренняя ошибка");
        }
    }

    @Test
    public void handleGenericException_ShouldReturnErrorResponse() throws Exception {
        mockMvc.perform(get("/test-exception"))
                .andExpect(status().isInternalServerError());
    }
}