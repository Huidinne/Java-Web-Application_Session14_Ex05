package org.example.ex_05;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.console.enabled=false")
class Ex05ApplicationTests {

    @Test
    void contextLoads() {
    }

}
