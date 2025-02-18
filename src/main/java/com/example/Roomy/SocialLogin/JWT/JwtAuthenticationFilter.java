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
    import org.springframework.web.filter.OncePerRequestFilter;

    import java.io.IOException;

    @Component
    @RequiredArgsConstructor
    public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtTokenProvider jwtTokenProvider;
        private final UserRepository userRepository;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            // 헤더에서 JWT 토큰 추출
            String token = jwtTokenProvider.getTokenFromRequest(request);
            System.out.println("요청 URL: " + request.getRequestURI());
            System.out.println("Authorization 헤더에서 추출한 토큰: " + token);

            if (token != null) {
                if (jwtTokenProvider.validateToken(token)) {
                    // Access Token이 유효한 경우
                    String userId = jwtTokenProvider.getUserIdFromToken(token);

                    // 유저가 존재하는지 확인
                    User user = userRepository.findById(Long.parseLong(userId)).orElse(null);
                    if (user != null) {
                        // 계정이 정지된 경우 응답 반환 (차단)
                        if (user.getStatus() == UserActivity.BAN) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("해당 계정은 신고 누적으로 인해 정지되었습니다.");
                            return;
                        }

                        // 정상 계정이면 SecurityContextHolder에 인증 정보 설정
                        var authentication = jwtTokenProvider.getAuthentication(userId);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("JWT 검증 성공! 사용자 인증 완료: " + authentication.getName());
                    } else {
                        System.out.println("DB에서 해당 사용자를 찾을 수 없음.");
                    }
                } else {
                    System.out.println("JWT 검증 실패! Refresh Token을 이용한 재발급 시도");

                    // Access Token이 유효하지 않으면, Refresh Token 검증 시도
                    try {
                        String newAccessToken = jwtTokenProvider.recreateAccessToken(token);
                        response.setHeader("Authorization", "Bearer " + newAccessToken);
                        System.out.println("새로운 Access Token 발급 완료: " + newAccessToken);
                    } catch (RuntimeException e) {
                        System.out.println("Refresh Token 검증 실패: " + e.getMessage());

                        // Refresh Token이 유효하지 않으면 SecurityContextHolder를 초기화 (로그아웃)
                        SecurityContextHolder.clearContext();

                        // **하지만 API 요청을 차단하지 않음
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write(e.getMessage());
                    }
                }
            } else {
                System.out.println("Authorization 헤더가 없음. 인증 없이 API 요청 진행");
            }

            // 다음 필터로 요청 전달 (JWT 오류가 있어도 API 요청 차단되지 않음)
            filterChain.doFilter(request, response);
        }
    }
