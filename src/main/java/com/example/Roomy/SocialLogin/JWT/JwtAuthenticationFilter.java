    package com.example.Roomy.SocialLogin.JWT;


    import com.example.Roomy.Report.Entity.UserActivity;
    import com.example.Roomy.SocialLogin.Entity.User;
    import com.example.Roomy.SocialLogin.Repository.UserRepository;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;

    import java.io.IOException;

    @Component
    @RequiredArgsConstructor
    public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtTokenProvider jwtTokenProvider;
        private final UserRepository userRepository;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

            // 헤더에서 JWT 토큰 추출
            String token = jwtTokenProvider.getTokenFromRequest(request);

            if (token != null) {
                if (jwtTokenProvider.validateToken(token)) {
                    // Access Token이 유효한 경우
                    //토큰에서 userid 추출
                    String userId = jwtTokenProvider.getUserIdFromToken(token);

                    User user= userRepository.findById(Long.parseLong(userId)) .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

                    //계정이 정지된 경우 요청 차단
                    if (user.getStatus() == UserActivity.BAN) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("해당 계정은 신고 누적으로 인해 정지되었습니다.");
                        return;
                    }

                    //정상 계정인 경우 SecurityContext에 인증 정보 설정
                    var authentication = jwtTokenProvider.getAuthentication(userId);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // Access Token이 유효하지 않을 때 Refresh Token 검증 및 Access Token 재발급 시도
                    try {
                        String newAccessToken = jwtTokenProvider.recreateAccessToken(token);
                        // 새로운 Access Token을 응답 헤더에 추가
                        response.setHeader("Authorization", "Bearer " + newAccessToken);
                    } catch (RuntimeException e) {
                        // Refresh Token 검증 실패 또는 재발급 실패
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write(e.getMessage());
                        return;
                    }
                }
            }

            // 다음 필터로 요청 전달
            filterChain.doFilter(request, response);
        }
    }
