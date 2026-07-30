package com.onuryigitkocaturk.query_monitor.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// bu SecurityConfig'te en öne koy dediğimiz filtredir. her istekte bir kez çalışıp
// gelen tokenı çözer, kullanıcı kimliğini SecurityContextHolder'a yerleştirir.
// @Component: "bu sınıfı senin yerine ben inşaa edeyim
// @Bean: "ben bu nesneyi elle inşaa ettim, sen sadece yönet.


// Authorization header'ından token'ı al.
// Token yoksa, hiçbir şey yapmadan isteği devam ettir (kimliksiz).
// Token varsa içindeki username'i çöz, veritabanından o kullanıcıyı taze olarak çek (güncel rolüyle).
// Token gerçekten geçerliyse (imza doğru + süresi dolmamış), bu bilgiyi SecurityContextHolder'a yaz —
// böylece bu isteğin geri kalanı boyunca "bu istek şu kullanıcıya ait" bilgisi hazır olur.
// Ne olursa olsun, isteği zincirdeki bir sonraki adıma gönder.
@Component
public class JwtAuthFilter extends OncePerRequestFilter { // OncePerRequestFilter, her istekte tam olarak 1 kez
                                                          // çalıştırılacağını garanti eder.

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    // token yoksa kimliksiz olarak devam eder
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.isTokenValid(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
