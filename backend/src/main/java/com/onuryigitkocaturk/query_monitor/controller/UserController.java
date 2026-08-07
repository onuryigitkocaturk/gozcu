package com.onuryigitkocaturk.query_monitor.controller;

import com.onuryigitkocaturk.query_monitor.dto.LoginRequest;
import com.onuryigitkocaturk.query_monitor.dto.LoginResponse;
import com.onuryigitkocaturk.query_monitor.dto.RegisterRequest;
import com.onuryigitkocaturk.query_monitor.dto.UserResponse;
import com.onuryigitkocaturk.query_monitor.dto.VerifyLoginCodeRequest;
import com.onuryigitkocaturk.query_monitor.mapper.UserMapper;
import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.security.DeviceTrustService;
import com.onuryigitkocaturk.query_monitor.security.JwtUtil;
import com.onuryigitkocaturk.query_monitor.service.LoginVerificationService;
import com.onuryigitkocaturk.query_monitor.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private static final String DEVICE_TOKEN_COOKIE = "device_token";

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final DeviceTrustService deviceTrustService;
    private final LoginVerificationService loginVerificationService;

    public UserController(UserService userService, UserMapper userMapper,
                           AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                           DeviceTrustService deviceTrustService,
                           LoginVerificationService loginVerificationService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.deviceTrustService = deviceTrustService;
        this.loginVerificationService = loginVerificationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                                @CookieValue(name = DEVICE_TOKEN_COOKIE, required = false) String deviceToken) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userService.getByUsername(request.getUsername());

        if (deviceTrustService.isTrusted(user.getId(), deviceToken)) {
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(new LoginResponse(token, false, null));
        }

        // bilinmeyen cihaz - JWT hemen verilmiyor, once mail dogrulamasi gerekiyor.
        String verificationToken = loginVerificationService.startVerification(user);
        return ResponseEntity.ok(new LoginResponse(null, true, verificationToken));
    }

    @PostMapping("/verify-login-code")
    public ResponseEntity<LoginResponse> verifyLoginCode(@Valid @RequestBody VerifyLoginCodeRequest request,
                                                          HttpServletResponse response) {
        User user = loginVerificationService.verifyCode(request.getVerificationToken(), request.getCode());

        String newDeviceToken = deviceTrustService.trustNewDevice(user);
        ResponseCookie cookie = ResponseCookie.from(DEVICE_TOKEN_COOKIE, newDeviceToken)
                .httpOnly(true)
                .secure(false) // production'da HTTPS ile true olmali
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(90))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new LoginResponse(token, false, null));
    }
}
