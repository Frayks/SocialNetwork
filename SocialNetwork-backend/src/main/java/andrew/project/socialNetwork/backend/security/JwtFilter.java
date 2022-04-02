package andrew.project.socialNetwork.backend.security;

import andrew.project.socialNetwork.backend.api.constants.TokenType;
import andrew.project.socialNetwork.backend.configuration.SecurityConfig;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger(JwtFilter.class);

    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityConfig.LOGIN_ENDPOINT.equals(request.getServletPath()) ||
                SecurityConfig.LOGOUT_ENDPOINT.equals(request.getServletPath()) ||
                SecurityConfig.REGISTRATION_ENDPOINT.equals(request.getServletPath()) ||
                SecurityConfig.REFRESH_TOKEN_ENDPOINTS.equals(request.getServletPath()) ||
                SecurityConfig.CONFIRM_ENDPOINTS.equals(request.getServletPath()) ||
                SecurityConfig.RESTORE_ENDPOINTS.equals(request.getServletPath()) ||
                SecurityConfig.RESET_PASSWORD_ENDPOINTS.equals(request.getServletPath()) ||
                SecurityConfig.WS_ENDPOINTS.equals(request.getServletPath())
        ) {
            filterChain.doFilter(request, response);
        } else {
            try {
                String accessToken = jwtProvider.resolveToken(request);
                if (accessToken == null) {
                    throw new Exception("Access token is missing!");
                }
                DecodedJWT decodedJWT = jwtProvider.verifyToken(accessToken, TokenType.ACCESS_TOKEN);
                Authentication authentication = jwtProvider.getAuthentication(decodedJWT);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
    }
}
