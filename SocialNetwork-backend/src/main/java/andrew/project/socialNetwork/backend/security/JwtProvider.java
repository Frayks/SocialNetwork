package andrew.project.socialNetwork.backend.security;

import andrew.project.socialNetwork.backend.api.constants.JwtConstants;
import andrew.project.socialNetwork.backend.api.constants.RoleName;
import andrew.project.socialNetwork.backend.api.constants.TokenType;
import andrew.project.socialNetwork.backend.api.entities.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider implements InitializingBean {

    @Value("${jwt.secret_key}")
    private String secretKey;
    @Value("${jwt.access_token_expiration_time}")
    private long accessTokenExpirationTime;
    @Value("${jwt.refresh_token_expiration_time}")
    private long refreshTokenExpirationTime;

    private UserDetailsService userDetailsService;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @Override
    public void afterPropertiesSet() {
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
        verifier = JWT.require(algorithm).build();
    }

    public String createAccessToken(String username, List<String> roles) throws IllegalArgumentException, JWTCreationException {
        Date expiresAt = new Date(System.currentTimeMillis() + accessTokenExpirationTime);
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(expiresAt)
                .withClaim(JwtConstants.ROLES, roles)
                .withClaim(JwtConstants.TOKEN_TYPE, TokenType.ACCESS_TOKEN.name())
                .sign(algorithm);
    }

    public String createRefreshToken(String username) throws IllegalArgumentException, JWTCreationException {
        Date expiresAt = new Date(System.currentTimeMillis() + refreshTokenExpirationTime);
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(expiresAt)
                .withClaim(JwtConstants.TOKEN_TYPE, TokenType.REFRESH_TOKEN.name())
                .sign(algorithm);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(JwtConstants.TOKEN_HEADER)) {
            return bearerToken.substring(JwtConstants.TOKEN_HEADER.length());
        }
        return null;
    }

    public DecodedJWT verifyToken(String token, TokenType tokenType) throws JWTVerificationException {
        DecodedJWT decodedJWT = verifier.verify(token);
        if (!decodedJWT.getClaim(JwtConstants.TOKEN_TYPE).as(String.class).equals(tokenType.name())) {
            throw new JWTVerificationException("Wrong token type!");
        }
        return decodedJWT;
    }

    public Authentication getAuthentication(DecodedJWT decodedJWT) throws UsernameNotFoundException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public String getUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public List<RoleName> getRoles(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim(JwtConstants.ROLES).asList(RoleName.class);
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
