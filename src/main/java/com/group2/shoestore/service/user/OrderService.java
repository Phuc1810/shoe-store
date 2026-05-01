package com.group2.shoestore.service.user;

import com.group2.shoestore.dto.request.CheckoutRequest;
import com.group2.shoestore.dto.response.CartItemResponse;
import com.group2.shoestore.dto.response.CartResponse;
import com.group2.shoestore.dto.response.OrderItemResponse;
import com.group2.shoestore.dto.response.OrderResponse;
import com.group2.shoestore.entity.Cart;
import com.group2.shoestore.entity.CartItem;
import com.group2.shoestore.entity.Order;
import com.group2.shoestore.entity.OrderItem;
import com.group2.shoestore.entity.ProductVariant;
import com.group2.shoestore.exception.BadRequestException;
import com.group2.shoestore.exception.ResourceNotFoundException;
import com.group2.shoestore.repository.CartItemRepository;
import com.group2.shoestore.repository.CartRepository;
import com.group2.shoestore.repository.OrderItemRepository;
import com.group2.shoestore.repository.OrderRepository;
import com.group2.shoestore.repository.ProductVariantRepository;
import com.group2.shoestore.util.OrderCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private static final Long DEMO_USER_ID = 2L;
    private static final String PAYMENT_METHOD_COD = "COD";
    private static final String PAYMENT_METHOD_BANK_QR = "BANK_QR";
    private static final String PAYMENT_STATUS_PENDING = "PENDING";
    private static final String PAYMENT_STATUS_PAID = "PAID";
    private static final String ORDER_STATUS_PENDING_PAYMENT = "PENDING_PAYMENT";
    private static final String ORDER_STATUS_CONFIRMED = "CONFIRMED";
    private static final String ORDER_STATUS_COMPLETED = "COMPLETED";
    private static final String ORDER_STATUS_CANCELLED = "CANCELLED";
    private static final Set<String> VALID_PAYMENT_METHODS = Set.of(PAYMENT_METHOD_COD, PAYMENT_METHOD_BANK_QR);
    private static final String PLACEHOLDER_IMAGE_URL = "/images/logo.png";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional(readOnly = true)
    public CartResponse getCheckoutCart() {
        Cart cart = getDemoUserCart();
        List<CartItem> cartItems = cartItemRepository.findByCartIdOrderByIdDesc(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Giỏ hàng đang trống");
        }

        List<CartItemResponse> items = cartItems.stream()
                .map(this::toCartItemResponse)
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(items)
                .totalAmount(totalAmount)
                .build();
    }

    @Transactional
    public OrderResponse createOrder(CheckoutRequest request) {
        validateCheckoutRequest(request);

        Cart cart = getDemoUserCart();
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Giỏ hàng đang trống");
        }

        cartItems.forEach(this::validateCartItemStock);

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .user(cart.getUser())
                .orderCode(OrderCodeGenerator.generate())
                .receiverName(request.getReceiverName().trim())
                .receiverPhone(request.getReceiverPhone().trim())
                .shippingAddress(request.getShippingAddress().trim())
                .note(normalizeBlank(request.getNote()))
                .totalAmount(totalAmount)
                .finalAmount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PAYMENT_STATUS_PENDING)
                .orderStatus(resolveInitialOrderStatus(request.getPaymentMethod()))
                .createdAt(now)
                .updatedAt(now)
                .build();
        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            ProductVariant productVariant = cartItem.getProductVariant();
            BigDecimal subtotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .productVariant(productVariant)
                    .productName(productVariant.getProduct().getName())
                    .color(productVariant.getColor())
                    .size(productVariant.getSize())
                    .unitPrice(cartItem.getUnitPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .build();
            orderItemRepository.save(orderItem);

            int stockBefore = productVariant.getStockQuantity();
            int stockAfter = stockBefore - cartItem.getQuantity();
            log.debug(
                    "Creating order: variantId={}, stockBefore={}, quantity={}, stockAfter={}",
                    productVariant.getId(),
                    stockBefore,
                    cartItem.getQuantity(),
                    stockAfter
            );
            productVariant.setStockQuantity(stockAfter);
            productVariantRepository.save(productVariant);
        }

        cartItemRepository.deleteByCartId(cart.getId());
        cart.setUpdatedAt(now);
        cartRepository.save(cart);

        return getOrderResponse(savedOrder.getId());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderResponse(Long orderId) {
        Order order = getDemoUserOrder(orderId);
        List<OrderItemResponse> items = orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(this::toOrderItemResponse)
                .toList();

        return toOrderResponse(order, items);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderHistory() {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(DEMO_USER_ID)
                .stream()
                .map(order -> toOrderResponse(order, List.of()))
                .toList();
    }

    @Transactional
    public void confirmBankQrPayment(Long orderId) {
        Order order = getDemoUserOrder(orderId);
        if (!PAYMENT_METHOD_BANK_QR.equals(order.getPaymentMethod())) {
            throw new BadRequestException("Đơn hàng không dùng phương thức thanh toán QR");
        }
        if (!ORDER_STATUS_PENDING_PAYMENT.equals(order.getOrderStatus())) {
            throw new BadRequestException("Đơn hàng không ở trạng thái chờ thanh toán");
        }

        order.setPaymentStatus(PAYMENT_STATUS_PAID);
        order.setOrderStatus(ORDER_STATUS_CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public void validateBankQrPaymentPage(Long orderId) {
        Order order = getDemoUserOrder(orderId);
        if (!PAYMENT_METHOD_BANK_QR.equals(order.getPaymentMethod())) {
            throw new BadRequestException("Đơn hàng không dùng phương thức thanh toán QR");
        }
    }

    private Cart getDemoUserCart() {
        return cartRepository.findByUserId(DEMO_USER_ID)
                .orElseThrow(() -> new BadRequestException("Giỏ hàng đang trống"));
    }

    private Order getDemoUserOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        if (!DEMO_USER_ID.equals(order.getUser().getId())) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng");
        }
        return order;
    }

    private void validateCheckoutRequest(CheckoutRequest request) {
        if (request == null) {
            throw new BadRequestException("Thông tin đặt hàng không hợp lệ");
        }
        if (request.getReceiverName() == null || request.getReceiverName().isBlank()) {
            throw new BadRequestException("Vui lòng nhập tên người nhận");
        }
        if (request.getReceiverPhone() == null || request.getReceiverPhone().isBlank()) {
            throw new BadRequestException("Vui lòng nhập số điện thoại");
        }
        if (request.getShippingAddress() == null || request.getShippingAddress().isBlank()) {
            throw new BadRequestException("Vui lòng nhập địa chỉ nhận hàng");
        }
        if (!VALID_PAYMENT_METHODS.contains(request.getPaymentMethod())) {
            throw new BadRequestException("Phương thức thanh toán không hợp lệ");
        }
    }

    private void validateCartItemStock(CartItem cartItem) {
        ProductVariant productVariant = cartItem.getProductVariant();
        Integer stockQuantity = productVariant.getStockQuantity();
        if (stockQuantity == null || stockQuantity < cartItem.getQuantity()) {
            throw new BadRequestException("Sản phẩm " + productVariant.getProduct().getName() + " không đủ tồn kho");
        }
    }

    private String resolveInitialOrderStatus(String paymentMethod) {
        if (PAYMENT_METHOD_BANK_QR.equals(paymentMethod)) {
            return ORDER_STATUS_PENDING_PAYMENT;
        }
        return ORDER_STATUS_CONFIRMED;
    }

    private String normalizeBlank(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        ProductVariant productVariant = cartItem.getProductVariant();
        BigDecimal subtotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .productVariantId(productVariant.getId())
                .productId(productVariant.getProduct().getId())
                .productName(productVariant.getProduct().getName())
                .imageUrl(resolveImageUrl(productVariant))
                .color(productVariant.getColor())
                .size(productVariant.getSize())
                .unitPrice(cartItem.getUnitPrice())
                .quantity(cartItem.getQuantity())
                .subtotal(subtotal)
                .stockQuantity(productVariant.getStockQuantity())
                .build();
    }

    private OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .productName(orderItem.getProductName())
                .color(orderItem.getColor())
                .size(orderItem.getSize())
                .unitPrice(orderItem.getUnitPrice())
                .quantity(orderItem.getQuantity())
                .subtotal(orderItem.getSubtotal())
                .build();
    }

    private OrderResponse toOrderResponse(Order order, List<OrderItemResponse> items) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .totalAmount(order.getTotalAmount())
                .finalAmount(order.getFinalAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .paymentStatusText(resolvePaymentStatusText(order.getPaymentStatus()))
                .orderStatusText(resolveOrderStatusText(order.getOrderStatus()))
                .paymentStatusBadgeClass(resolvePaymentStatusBadgeClass(order.getPaymentStatus()))
                .orderStatusBadgeClass(resolveOrderStatusBadgeClass(order.getOrderStatus()))
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    private String resolvePaymentStatusText(String paymentStatus) {
        if (PAYMENT_STATUS_PAID.equals(paymentStatus)) {
            return "Đã thanh toán";
        }
        return "Chờ thanh toán";
    }

    private String resolveOrderStatusText(String orderStatus) {
        return switch (orderStatus) {
            case ORDER_STATUS_PENDING_PAYMENT -> "Chờ thanh toán";
            case ORDER_STATUS_CONFIRMED -> "Đã xác nhận";
            case ORDER_STATUS_COMPLETED -> "Hoàn thành";
            case ORDER_STATUS_CANCELLED -> "Đã hủy";
            default -> orderStatus;
        };
    }

    private String resolvePaymentStatusBadgeClass(String paymentStatus) {
        if (PAYMENT_STATUS_PAID.equals(paymentStatus)) {
            return "text-bg-success";
        }
        return "text-bg-warning";
    }

    private String resolveOrderStatusBadgeClass(String orderStatus) {
        return switch (orderStatus) {
            case ORDER_STATUS_PENDING_PAYMENT -> "text-bg-warning";
            case ORDER_STATUS_CONFIRMED -> "text-bg-primary";
            case ORDER_STATUS_COMPLETED -> "text-bg-success";
            case ORDER_STATUS_CANCELLED -> "text-bg-danger";
            default -> "text-bg-secondary";
        };
    }

    private String resolveImageUrl(ProductVariant productVariant) {
        if (productVariant.getImageUrl() == null || productVariant.getImageUrl().isBlank()) {
            return PLACEHOLDER_IMAGE_URL;
        }
        return productVariant.getImageUrl();
    }
}
