package net.poulp.security.api.controller;

import net.poulp.security.model.AuthenticationRequest;
import net.poulp.security.model.JwtTokens;
import net.poulp.security.model.RefreshToken;
import net.poulp.security.service.AuthenticationService;
import net.poulp.security.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private static final String JWT_TOKEN_HEADER_NAME = "X-AUTH-TOKEN";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @PostMapping(value = {"/auth"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authenticationRequest.username, authenticationRequest.password);
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if(authentication != null && authentication.isAuthenticated()) {
            JwtTokens tokens = jwtTokenService.createTokens(authentication);
            return ResponseEntity.ok().body(tokens);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
    }

    @PostMapping(value = "/auth/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshToken refreshToken) {
        try {
            String jwtToken = jwtTokenService.refreshJwtToken(refreshToken.token);
            return ResponseEntity.ok().body(jwtToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
