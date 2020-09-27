package com.bs.epic.battleships.verification;

import com.bs.epic.battleships.rest.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final String HEADER_STRING;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public JwtAuthFilter(@Value("${jwt.auth.header}") String HEADER_STRING, JwtUtil jwtUtil, AuthService authService) {
        this.HEADER_STRING = HEADER_STRING;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException
    {
        var header = req.getHeader(HEADER_STRING);
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        var token = verifyJwt(header, req);
        if (token != null) SecurityContextHolder.getContext().setAuthentication(token);

        chain.doFilter(req, res);
    }

    public UsernamePasswordAuthenticationToken verifyJwt(String header, HttpServletRequest req) {
        var jwt = header.substring(7);
        var username = jwtUtil.extractUsername(jwt);

        if (username != null) {
            var oUser = authService.getByUsername(username);
            if (oUser.isPresent()) {
                var user = oUser.get();
                if (jwtUtil.validateToken(jwt, user)) {
                    var grantedAuthorities = new HashSet<GrantedAuthority>();
                    var token = new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities);
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    return token;
                }
            }
        }

        return null;
    }
}
