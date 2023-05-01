package com.example.cashin.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.stereotype.Component;

/**
 * Bean responsible for recording every Http request received by this application.
 * Since it is only meant to log the request the underlying storage is in memory.
 */
@Component
public class LoggingHttpExchangeRepository extends InMemoryHttpExchangeRepository {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingHttpExchangeRepository.class);

    private final ObjectMapper objectMapper;

    public LoggingHttpExchangeRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void add(HttpExchange exchange) {
        String jsonString = toJsonString(exchange);
        if (jsonString != null) {
            LOG.info(jsonString);
        }
        super.add(exchange);
    }

    private String toJsonString(HttpExchange exchange) {
        try {
            return objectMapper.writeValueAsString(exchange);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
