package andrew.project.socialNetwork.backend.configuration;

import andrew.project.socialNetwork.backend.security.AuthenticationFilter;
import andrew.project.socialNetwork.backend.security.JwtConfigurer;
import andrew.project.socialNetwork.backend.security.JwtFilter;
import andrew.project.socialNetwork.backend.security.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtProvider jwtProvider;

    public static final String LOGIN_ENDPOINT = "/api/login";
    public static final String REGISTRATION_ENDPOINT = "/api/registration";
    public static final String LOGOUT_ENDPOINT = "/api/logout";
    public static final String REFRESH_TOKEN_ENDPOINTS = "/api/refreshToken";
    public static final String CONFIRM_ENDPOINTS = "/api/confirm";
    public static final String RESTORE_ENDPOINTS = "/api/restore";
    public static final String RESET_PASSWORD_ENDPOINTS = "/api/resetPassword";

    public SecurityConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers(
                        LOGIN_ENDPOINT,
                        REFRESH_TOKEN_ENDPOINTS,
                        LOGOUT_ENDPOINT,
                        REGISTRATION_ENDPOINT,
                        CONFIRM_ENDPOINTS,
                        RESTORE_ENDPOINTS,
                        RESET_PASSWORD_ENDPOINTS
                ).permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .apply(jwtConfigurer());
    }

    @Bean
    public JwtConfigurer jwtConfigurer() throws Exception {
        JwtFilter jwtFilter = new JwtFilter(jwtProvider);
        return new JwtConfigurer(jwtFilter, corsFilter(), authenticationFilter());
    }

    AuthenticationFilter authenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManagerBean(), jwtProvider);
        authenticationFilter.setFilterProcessesUrl(LOGIN_ENDPOINT);
        return authenticationFilter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("PATCH");
        config.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
