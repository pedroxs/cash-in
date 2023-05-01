package com.example.cashin;

import com.example.cashin.web.rest.vm.LoadRequestVM;
import com.example.cashin.web.rest.vm.LoadResponseVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoadIntegrationTests {

    private TestRestTemplate restTemplate;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        restTemplate = new TestRestTemplate(new RestTemplateBuilder().rootUri("http://localhost:8080"))
                .withBasicAuth("user", "password");
        mapper = Jackson2ObjectMapperBuilder.json().build();
    }

    @Test
    void loadFile() throws Exception {
        File inputFile = new File("./src/test/resources/input.txt");
        File outputFile = new File("./src/test/resources/output.txt");
        BufferedReader reader = Files.newBufferedReader(outputFile.toPath());
        List<LoadRequestVM> lines = Files.lines(inputFile.toPath())
                .map(line -> safeParseLine(line, LoadRequestVM.class))
                .toList();
        lines.forEach(line -> {
            ResponseEntity<LoadResponseVM> response = restTemplate.postForEntity("/load", line, LoadResponseVM.class);
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            if (response.hasBody()) {
                try {
                    assertThat(response.getBody()).isEqualTo(safeParseLine(reader.readLine(), LoadResponseVM.class));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        assertThat(reader.readLine()).isNull();
    }

    private <T> T safeParseLine(String line, Class<T> clazz) {
        try {
            return mapper.readValue(line, clazz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
