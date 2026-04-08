package nguyennhatquan.springbootreview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nguyennhatquan.springbootreview.dto.CartItemResponse;
import nguyennhatquan.springbootreview.dto.CartResponse;
import nguyennhatquan.springbootreview.entity.Cart;
import nguyennhatquan.springbootreview.entity.CartItem;
import nguyennhatquan.springbootreview.entity.Product;
import nguyennhatquan.springbootreview.exception.ResourceNotFoundException;
import nguyennhatquan.springbootreview.repository.CartItemRepository;
import nguyennhatquan.springbootreview.repository.CartRepository;
import nguyennhatquan.springbootreview.repository.ProductRepository;
import nguyennhatquan.springbootreview.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse addItem(Long userId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElse(CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(0)
                        .build());

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemRepository.save(cartItem);

        cart = cartRepository.findByUserId(userId).get();

        log.info("Added {} of product {} to cart for user {}", quantity, productId, userId);

        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(Long userId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (quantity <= 0) {
            cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);
        } else {
            if (product.getStock() < quantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        cart = cartRepository.findByUserId(userId).get();

        log.info("Updated item {} in cart for user {}", productId, userId);

        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);

        cart = cartRepository.findByUserId(userId).get();

        log.info("Removed item {} from cart for user {}", productId, userId);

        return mapToResponse(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        if (cart.getItems() != null) {
            cart.getItems().clear();
            cartRepository.save(cart);
        }

        log.info("Cart cleared for user {}", userId);
    }

    private CartResponse mapToResponse(Cart cart) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            for (CartItem item : cart.getItems()) {
                BigDecimal itemTotal = item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                totalPrice = totalPrice.add(itemTotal);
            }
        }

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(cart.getItems() != null ? cart.getItems().stream()
                        .map(this::mapItemToResponse).toList() : new ArrayList<>())
                .totalPrice(totalPrice)
                .build();
    }

    private CartItemResponse mapItemToResponse(CartItem item) {
        return CartItemResponse.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .price(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}
