package com.example.cashin.web.filter;

public final class CommonFilterParameters {

    static final String[] EXCLUDED_ENDPOINTS = new String[]{
            "/error",
            "/actuator/**",
            "/h2-console/**",
            "/webjars/**",
            "/favicon.ico"
    };

    private CommonFilterParameters() {
    }

}
