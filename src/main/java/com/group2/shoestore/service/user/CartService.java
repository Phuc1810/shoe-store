package com.group2.shoestore.service.user;

import com.group2.shoestore.dto.response.CartItemResponse;
import com.group2.shoestore.dto.response.CartResponse;
import com.group2.shoestore.entity.Cart;
import com.group2.shoestore.entity.CartItem;
import com.group2.shoestore.entity.ProductVariant;
import com.group2.shoestore.entity.User;
import com.group2.shoestore.exception.BadRequestException;
import com.group2.shoestore.exception.ResourceNotFoundException;
import com.group2.shoestore.repository.CartItemRepository;
import com.group2.shoestore.repository.CartRepository;
import com.group2.shoestore.repository.ProductVariantRepository;
import com.group2.shoestore.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String PLACEHOLDER_IMAGE_URL = "/images/logo.png";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public void addToCart(Long productVariantId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Số lượng phải lớn hơn 0");
        }

        ProductVariant productVariant = productVariantRepository.findById(productVariantId)
                .filter(variant -> ACTIVE_STATUS.equals(variant.getStatus()))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm"));

        Cart cart = getOrCreateCart();
        CartItem cartItem = cartItemRepository.findByCartIdAndProductVariantId(cart.getId(), productVariantId)
                .orElse(null);

        int currentQuantity = cartItem == null ? 0 : cartItem.getQuantity();
        int newQuantity = currentQuantity + quantity;
        validateStock(productVariant, newQuantity);

        if (cartItem == null) {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(productVariant)
                    .quantity(quantity)
                    .unitPrice(productVariant.getPrice())
                    .build();
        } else {
            cartItem.setQuantity(newQuantity);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void updateCartItem(Long cartItemId, Integer quantity) {
        CartItem cartItem = getCurrentUserCartItem(cartItemId);

        if (quantity == null || quantity <= 0) {
            deleteCartItem(cartItem);
            return;
        }

        validateStock(cartItem.getProductVariant(), quantity);
        cartItem.setQuantity(quantity);
        cartItem.getCart().setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cartItem.getCart());
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeCartItem(Long cartItemId) {
        CartItem cartItem = getCurrentUserCartItem(cartItemId);
        deleteCartItem(cartItem);
    }

    @Transactional(readOnly = true)
    public CartResponse getCurrentCart() {
        Long currentUserId = currentUserService.getCurrentUserId();
        return cartRepository.findByUserId(currentUserId)
                .map(this::toCartResponse)
                .orElseGet(() -> CartResponse.builder()
                        .items(List.of())
                        .totalAmount(BigDecimal.ZERO)
                        .build());
    }

    private Cart getOrCreateCart() {
        Long currentUserId = currentUserService.getCurrentUserId();
        return cartRepository.findByUserId(currentUserId)
                .orElseGet(() -> {
                    User user = currentUserService.getCurrentUser();
                    LocalDateTime now = LocalDateTime.now();
                    Cart cart = Cart.builder()
                            .user(user)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return cartRepository.save(cart);
                });
    }

    private void validateStock(ProductVariant productVariant, int quantity) {
        Integer stockQuantity = productVariant.getStockQuantity();
        if (stockQuantity == null || stockQuantity < quantity) {
            throw new BadRequestException("Số lượng tồn kho không đủ");
        }
    }

    private CartItem getCurrentUserCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));

        Long cartUserId = cartItem.getCart().getUser().getId();
        if (!currentUserService.getCurrentUserId().equals(cartUserId)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng");
        }

        return cartItem;
    }

    private void deleteCartItem(CartItem cartItem) {
        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    private CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> items = cartItemRepository.findByCartIdOrderByIdDesc(cart.getId())
                .stream()
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

    private String resolveImageUrl(ProductVariant productVariant) {
        if (productVariant.getImageUrl() == null || productVariant.getImageUrl().isBlank()) {
            return PLACEHOLDER_IMAGE_URL;
        }
        return productVariant.getImageUrl();
    }
}
