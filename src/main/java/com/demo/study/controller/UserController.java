package com.demo.study.controller;

import com.demo.study.entity.User;
import com.demo.study.jwt.JwtTokenProvider;
import com.demo.study.security.UserRepository;
import com.demo.study.service.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CookieUtil cookieUtil;
    // 회원가입
    @PostMapping("/join")
    public Long join(@RequestBody Map<String, String> user) {
        System.out.println("test");
        return userRepository.save(User.builder()
                .email(user.get("email"))
                .password(passwordEncoder.encode(user.get("password")))
                .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                .build()).getId();
    }

    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user, HttpServletResponse response) {
        System.out.println("email : " + user.get("email"));
        User member = userRepository.findByEmail(user.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        String token = jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
        cookieUtil.createCookie("token", token, response);
        return token;
    }

    @PostMapping("/user/test")
    public Map userResponseTest(HttpServletRequest req) {
        Map<String, String> result = new HashMap<>();
        result.put("result","user ok");
        System.out.println("??" + cookieUtil.getCookie(req, "token"));
        return result;
    }



    @PostMapping("/admin/test")
    public Map adminResponseTest() {
        Map<String, String> result = new HashMap<>();
        result.put("result","admin ok");
        return result;
    }
}