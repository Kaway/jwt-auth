package net.poulp.security.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import net.poulp.security.service.AuthenticationService;
import net.poulp.security.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationTokenFilter extends GenericFilterBean {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        final Optional<String> token = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));

        Authentication authentication = null;

        if(token.isPresent()) {

            try {
                Jws<Claims> claims = jwtTokenService.validateJwtToken(token.get());
                authentication = authenticationService.getAuthentication(claims);
            } catch (ExpiredJwtException exception) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "jwt.expired");
                return;
            } catch (Exception exception) {
                SecurityContextHolder.getContext().setAuthentication(null);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(servletRequest, servletResponse);
        SecurityContextHolder.getContext().setAuthentication(null); // Clean authentication after process

    }

}