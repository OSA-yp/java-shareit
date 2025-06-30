package ru.practicum.shareit.gateway.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class ErrorResponseTest {

    @Test
    void noStackTest(){

        ErrorResponse errorResponse = new ErrorResponse("error", "dis", null);

        Assertions.assertEquals("error", errorResponse.getError());

    }


}
