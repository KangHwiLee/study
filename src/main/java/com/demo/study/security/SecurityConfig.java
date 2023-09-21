package com.demo.study.security;

import com.demo.study.jwt.JwtAuthenticationFilter;
import com.demo.study.jwt.JwtTokenProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration

public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    public SecurityConfig(JwtTokenProvider jwtTokenProvider){
        this.jwtTokenProvider = jwtTokenProvider;
    }
    private final String[] allowedUrls = {"/css/**", "/img/**","/js/**","/scss/**","/vendor/**","/login","/join", "/register"};	// sign-up, sign-in 추가

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http
                 .httpBasic().disable() // rest api 만을 고려하여 기본 설정은 해제하겠습니다.
                 .csrf().disable() // csrf 보안 토큰 disable처리.
                 .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
                 .and()
                 .authorizeRequests()
                 .requestMatchers(allowedUrls).permitAll()
                 .requestMatchers("/admin/**").hasRole("ADMIN")            //권한별 접속 가능 페이지
                 .requestMatchers("/user/**").hasRole("USER")            //권한별 접속 가능 페이지
                 .anyRequest().authenticated()			//나머지 접속
                 .and()
                 .logout()
                 .permitAll()
                 .and()
                 .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                         UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}