package com.moayo.moayobackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@TestPropertySource(properties = {
        "ai.recommend.api-key=test-key",
        "ai.recommend.base-url=http://localhost:8080"
})
@SpringBootTest
class MoayoBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
