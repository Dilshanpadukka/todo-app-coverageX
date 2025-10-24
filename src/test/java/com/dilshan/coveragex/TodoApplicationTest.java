package com.dilshan.coveragex;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for TodoApplication
 * 
 * @author Dilshan
 * @version 1.0
 * @since 2025-10-24
 */
@SpringBootTest
@ActiveProfiles("test")
class TodoApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // If this test passes, it means all beans are properly configured and wired
    }
}
