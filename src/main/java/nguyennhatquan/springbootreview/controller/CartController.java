package nguyennhatquan.springbootreview.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nguyennhatquan.springbootreview.dto.*;
import nguyennhatquan.springbootreview.entity.User;
import nguyennhatquan.springbootreview.repository.UserRepository;
import nguyennhatquan.springbootreview.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication authentication) {
        log.info("Get cart for user: {}", authentication.getName());

        User user = userRepository.findByEmail(authentication.getName()).get();
        CartResponse data = cartService.getCart(user.getId());

        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Cart retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication) {
        log.info("Add item {} with quantity {} to cart", request.getProductId(), request.getQuantity());

        User user = userRepository.findByEmail(authentication.getName()).get();
        CartResponse data = cartService.addItem(user.getId(), request.getProductId(), request.getQuantity());

        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Item added to cart successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request,
            Authentication authentication) {
        log.info("Update item {} with quantity {}", productId, request.getQuantity());

        User user = userRepository.findByEmail(authentication.getName()).get();
        CartResponse data = cartService.updateItem(user.getId(), productId, request.getQuantity());

        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Item updated successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @PathVariable Long productId,
            Authentication authentication) {
        log.info("Remove item {} from cart", productId);

        User user = userRepository.findByEmail(authentication.getName()).get();
        CartResponse data = cartService.removeItem(user.getId(), productId);

        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Item removed successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> clearCart(Authentication authentication) {
        log.info("Clear cart for user: {}", authentication.getName());

        User user = userRepository.findByEmail(authentication.getName()).get();
        cartService.clearCart(user.getId());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(200)
                .message("Cart cleared successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}
