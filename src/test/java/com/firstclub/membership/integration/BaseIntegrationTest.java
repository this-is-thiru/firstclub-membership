package com.firstclub.membership.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    static MySQLContainer<?> mysql;

    static {
        if (isDockerAvailable()) {
            mysql = new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("firstclub")
                    .withUsername("test")
                    .withPassword("test");
            mysql.start();
        }
    }

    @AfterAll
    static void stopContainer() {
        if (mysql != null && mysql.isRunning()) {
            mysql.stop();
        }
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        if (mysql != null && mysql.isRunning()) {
            registry.add("spring.datasource.url", mysql::getJdbcUrl);
            registry.add("spring.datasource.username", mysql::getUsername);
            registry.add("spring.datasource.password", mysql::getPassword);
        }
    }

    @LocalServerPort
    private int port;

    protected RestTemplate getRestTemplate() {
        RestTemplate template = new RestTemplate();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        template.setRequestFactory(factory);
        return template;
    }

    protected String baseUrl() {
        return "http://localhost:" + port;
    }

    protected HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    static boolean isDockerAvailable() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (Exception e) {
            return false;
        }
    }
}
