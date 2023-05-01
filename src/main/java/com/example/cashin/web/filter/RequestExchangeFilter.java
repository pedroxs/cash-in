package com.example.cashin.web.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.Include;
import org.springframework.boot.actuate.web.exchanges.servlet.HttpExchangesFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Set;

@Component
public class RequestExchangeFilter extends HttpExchangesFilter {

    public RequestExchangeFilter(HttpExchangeRepository repository, Set<Include> includes) {
        super(repository, includes);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return Arrays.stream(CommonFilterParameters.EXCLUDED_ENDPOINTS)
                .anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()));
    }

}
