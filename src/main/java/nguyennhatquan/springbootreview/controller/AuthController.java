package nguyennhatquan.springbootreview.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nguyennhatquan.springbootreview.dto.ApiResponse;
import nguyennhatquan.springbootreview.dto.CreateUserRequest;
import nguyennhatquan.springbootreview.dto.LoginRequest;
import nguyennhatquan.springbootreview.dto.LoginResponse;
import nguyennhatquan.springbootreview.exception.UnauthorizedException;
import nguyennhatquan.springbootreview.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        log.info("Login request for email: {}", request.getEmail());

        LoginResponse loginResponse = userService.login(request);

        // Set access token in cookie with HttpOnly, Secure, and SameSite attributes
        String accessToken = loginResponse.getAccessToken();
        ResponseCookie cookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(false)  // Set to true in production with HTTPS
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Remove token from response body for security
        loginResponse.setAccessToken(null);
        loginResponse.setTokenType(null);

        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .code(200)
                .message("Login successful")
                .data(loginResponse)
                .timestamp(LocalDateTime.now())
                .path("/api/auth/login")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody CreateUserRequest request, HttpServletResponse response) {
        log.info("Register request for email: {}", request.getEmail());

        LoginResponse registerResponse = userService.register(request);

        // Set access token in cookie with HttpOnly, Secure, and SameSite attributes
        String accessToken = registerResponse.getAccessToken();
        ResponseCookie cookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(false)  // Set to true in production with HTTPS
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Remove token from response body for security
        registerResponse.setAccessToken(null);
        registerResponse.setTokenType(null);

        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .code(201)
                .message("Registration successful")
                .data(registerResponse)
                .timestamp(LocalDateTime.now())
                .path("/api/auth/register")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse reponse) {
        ResponseCookie cookie = ResponseCookie.from("access_token","")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        reponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(200)
                .message("Logout successful")
                .timestamp(LocalDateTime.now())
                .path("/api/auth/logout")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<LoginResponse>> getProfile(@CookieValue(value = "access_token", required = false) String accessToken) {
        log.info("Get profile request");

        if (accessToken == null || accessToken.isEmpty()) {
            throw new UnauthorizedException("Access token not found in cookie");
        }

        LoginResponse profileResponse = userService.getUserProfile(accessToken);

        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .code(200)
                .message("Profile retrieved successfully")
                .data(profileResponse)
                .timestamp(LocalDateTime.now())
                .path("/api/auth/profile")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
