package com.learning.emsmybatisliquibase.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        if (Boolean.TRUE.equals(request.getAttribute("tokenExpired"))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
        } else if (Boolean.TRUE.equals(request.getAttribute("invalidToken"))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        }
    }

}
