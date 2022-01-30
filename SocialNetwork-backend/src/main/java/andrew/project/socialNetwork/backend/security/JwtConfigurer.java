package andrew.project.socialNetwork.backend.security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtFilter jwtFilter;
    private final CorsFilter corsFilter;
    private final AuthenticationFilter authenticationFilter;

    public JwtConfigurer(JwtFilter jwtFilter, CorsFilter corsFilter, AuthenticationFilter authenticationFilter) {
        this.jwtFilter = jwtFilter;
        this.corsFilter = corsFilter;
        this.authenticationFilter = authenticationFilter;
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        httpSecurity.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
