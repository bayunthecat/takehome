package com.example.takehome;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.endpoint.ApiVersion;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TakehomeApplicationTests {

    @Test
    void contextLoads() {
        assertEquals(ApiVersion.LATEST, ApiVersion.V3);
    }
}
