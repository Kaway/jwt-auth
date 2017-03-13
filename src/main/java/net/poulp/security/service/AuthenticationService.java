package net.poulp.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import net.poulp.security.model.AuthenticationRequest;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {

	Authentication getAuthentication(Jws<Claims> request);
	Authentication authenticate(AuthenticationRequest authenticationRequest);

}
