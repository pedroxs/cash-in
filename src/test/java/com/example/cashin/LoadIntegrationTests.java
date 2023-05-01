package com.example.cashin;

import com.example.cashin.web.rest.vm.LoadRequestVM;
import com.example.cashin.web.rest.vm.LoadResponseVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

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
        List<LoadRequestVM> lines = Files.lines(inputFile.toPath())
                .limit(3)
                .map(line -> {
                    try {
                        return mapper.readValue(line, LoadRequestVM.class);
                    } catch (JsonProcessingException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .toList();
        Assertions.assertThat(lines).hasSize(3);
        lines.forEach(line -> {
            ResponseEntity<LoadResponseVM> response = restTemplate.postForEntity("/load", line, LoadResponseVM.class);
            System.out.println(response);
        });
    }
}
