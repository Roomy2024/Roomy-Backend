package com.example.Roomy.SocialLogin.Util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider){
        this.jwtTokenProvider=jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException{

        //헤더에서 JWT 토큰 추출
        String token = getTokenFromRequest(request);

        //토큰 유효성 검증
        if(token != null && jwtTokenProvider.validateToken(token)){
            String userid= jwtTokenProvider.getUserIdFromToken(token);

            var authentication = jwtTokenProvider.getAuthentication(userid);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request,response);
    }

    private String getTokenFromRequest(HttpServletRequest request){
        String bearerToken= request.getHeader("Authorization");

        if(bearerToken != null && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7); //bearer 제거
        }
        return null;
    }
}
