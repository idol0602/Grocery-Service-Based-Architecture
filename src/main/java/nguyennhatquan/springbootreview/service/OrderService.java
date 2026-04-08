package nguyennhatquan.springbootreview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nguyennhatquan.springbootreview.dto.OrderResponse;
import nguyennhatquan.springbootreview.dto.PageResponse;
import nguyennhatquan.springbootreview.entity.*;
import nguyennhatquan.springbootreview.exception.BadRequestException;
import nguyennhatquan.springbootreview.exception.ResourceNotFoundException;
import nguyennhatquan.springbootreview.repository.CartItemRepository;
import nguyennhatquan.springbootreview.repository.CartRepository;
import nguyennhatquan.springbootreview.repository.OrderRepository;
import nguyennhatquan.springbootreview.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getUserOrders(Long userId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Order> page = orderRepository.findByUserIdAndIsDeletedFalse(userId, pageable);

        log.debug("Fetched {} orders for user {} from page {}", page.getContent().size(), userId, pageNo);

        return buildPageResponse(page, pageNo, pageSize);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id, Long userId) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to this order");
        }

        return mapToResponse(order);
    }

    @Transactional
    public OrderResponse createOrderFromCart(Long userId, String shippingAddress) throws BadRequestException {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        for(CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if(product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName()
                        + " (available: " + product.getStock() + ", requested: " + cartItem.getQuantity() + ")");
            }
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .status(OrderStatus.PENDING)
                .shippingAddress(shippingAddress)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for(CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            product.setStock(product.getStock() - cartItem.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);
        log.info("Order created successfully with id: {} for user: {}", savedOrder.getId(), userId);
        return mapToResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setStatus(status);
        Order updated = orderRepository.save(order);

        log.info("Order {} status updated to {}", id, status);

        return mapToResponse(updated);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setIsDeleted(true);
        orderRepository.save(order);

        log.info("Order deleted (soft delete) with id: {}", id);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().toString())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private PageResponse<OrderResponse> buildPageResponse(Page<Order> page, int pageNo, int pageSize) {
        return PageResponse.<OrderResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).toList())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
}
