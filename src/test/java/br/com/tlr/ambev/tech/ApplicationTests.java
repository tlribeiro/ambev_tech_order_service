package br.com.tlr.ambev.tech;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class ApplicationTests {

    ApplicationTests() {

    }

    @Test
    void contextLoads() {
        Assert.isTrue(true, "");
    }

}
