package net.poulp.security.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import net.poulp.security.model.JwtTokens;
import net.poulp.security.model.UserDto;
import net.poulp.security.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private static final String USER_SECRET = "userSecret";

    @Value("${token.secret}")
    private String secret;

    @Autowired
    private UserDetailsService userService;

    @Override
    public JwtTokens createTokens(Authentication authentication) {

        String token;
        String refreshToken;

        UserDto user = (UserDto) authentication.getPrincipal();

        token = createToken(user);
        refreshToken = createRefreshToken(user);

        return new JwtTokens(token, refreshToken);
    }

    @Override
    public String createToken(UserDto user) {

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secret)
                .setClaims(buildUserClaims(user))
                .setExpiration(getTokenExpirationDate(false))
                .setIssuedAt(new Date())
                .compact();
    }

    @Override
    public String createRefreshToken(UserDto user) {

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secret)
                .setClaims(buildUserClaims(user))
                .setExpiration(getTokenExpirationDate(true))
                .setIssuedAt(new Date())
                .compact();
    }

    @Override
    public Jws<Claims> validateJwtToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
    }

    @Override
    public JwtTokens refreshJwtToken(String refreshToken) {
        Jws<Claims> claims = validateJwtRefreshToken(refreshToken);
        String newToken = createTokenFromClaims(claims);
        return new JwtTokens(newToken, refreshToken);
    }

    private String createTokenFromClaims(Jws<Claims> jws) {

        return Jwts.builder()
                .setClaims(jws.getBody())
                .signWith(SignatureAlgorithm.HS512, secret)
                .setExpiration(getTokenExpirationDate(false))
                .setIssuedAt(new Date())
                .compact();
    }

    private Jws<Claims> validateJwtRefreshToken(String token) {
        JwtParser parser = Jwts.parser().setSigningKey(secret);
        Jws<Claims> claims = parser.parseClaimsJws(token);

        UserDto user = (UserDto) userService.loadUserByUsername(claims.getBody().getSubject());

        return parser.require(USER_SECRET, user.getUserSecret()).parseClaimsJws(token);
    }

    private Date getTokenExpirationDate(boolean refreshToken) {
        Calendar calendar = Calendar.getInstance();

        if(refreshToken) {
            calendar.add(Calendar.MONTH, 1);
        } else {
            calendar.add(Calendar.MINUTE, 5);
        }

        return calendar.getTime();
    }

    private Claims buildUserClaims(UserDto user) {
        Claims claims = new DefaultClaims();

        claims.setSubject(String.valueOf(user.getId()));
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("roles", String.join(",", AuthorityUtils.authorityListToSet(user.getAuthorities())));
        claims.put(USER_SECRET, user.getUserSecret());

        return claims;
    }
}
