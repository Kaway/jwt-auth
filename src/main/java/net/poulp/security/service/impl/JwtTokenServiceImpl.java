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

    @Value("${token.secret}")
    private String secret;

    @Override
    public JwtTokens createTokens(Authentication authentication) {

        String token;
        token = createToken((UserDto) authentication.getPrincipal());

        return new JwtTokens(token);
    }

    @Override
    public String createToken(UserDto user) {

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secret)
                .setExpiration(getTokenExpirationDate())
                .setIssuedAt(new Date())
                .setClaims(buildUserClaims(user))
                .compact();
    }

    @Override
    public Jws<Claims> validateJwtToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
    }


    private Date getTokenExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        return calendar.getTime();
    }

    private Claims buildUserClaims(UserDto user) {
        Claims claims = new DefaultClaims();

        claims.setSubject(String.valueOf(user.getId()));
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("roles", String.join(",", AuthorityUtils.authorityListToSet(user.getAuthorities())));
        claims.put("salt", user.getSalt());

        return claims;
    }
}
