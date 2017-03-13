package net.poulp.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import net.poulp.security.model.JwtTokens;
import net.poulp.security.model.UserDto;
import org.springframework.security.core.Authentication;

public interface JwtTokenService {

    JwtTokens createTokens(Authentication authentication);
    String createToken(UserDto user);
    String createRefreshToken(UserDto user);

    String refreshJwtToken(String token);
    Jws<Claims> validateJwtToken(String token);

}
