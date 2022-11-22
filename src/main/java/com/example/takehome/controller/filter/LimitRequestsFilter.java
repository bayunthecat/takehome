package com.example.takehome.controller.filter;

import com.example.takehome.service.RequestLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LimitRequestsFilter implements Filter {

    private final RequestLimitService requestLimitService;

    public LimitRequestsFilter(RequestLimitService requestLimitService) {
        this.requestLimitService = requestLimitService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        final var principal = httpRequest.getUserPrincipal();
        if (principal != null && requestLimitService.checkRequestsLimitPerUser(principal.getName())) {
            writeTooManyRequests(httpResponse);
        } else if (requestLimitService.checkRequestsLimitPerIp(request.getRemoteAddr())) {
            writeTooManyRequests(httpResponse);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void writeTooManyRequests(HttpServletResponse response) {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    }
}