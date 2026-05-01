package com.group2.shoestore.controller.user;

import com.group2.shoestore.dto.request.CheckoutRequest;
import com.group2.shoestore.dto.response.OrderResponse;
import com.group2.shoestore.exception.BadRequestException;
import com.group2.shoestore.exception.ResourceNotFoundException;
import com.group2.shoestore.service.user.HomeService;
import com.group2.shoestore.service.user.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final HomeService homeService;

    @GetMapping("/orders")
    public String orderHistory(Model model) {
        model.addAttribute("home", homeService.getHomeData());
        model.addAttribute("orders", orderService.getOrderHistory());
        model.addAttribute("keyword", null);
        return "user/order-history";
    }

    @GetMapping("/orders/{orderId}")
    public String orderDetail(@PathVariable Long orderId, Model model) {
        model.addAttribute("home", homeService.getHomeData());
        model.addAttribute("order", orderService.getOrderResponse(orderId));
        model.addAttribute("keyword", null);
        return "user/order-detail";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("home", homeService.getHomeData());
            model.addAttribute("cart", orderService.getCheckoutCart());
            if (!model.containsAttribute("checkoutRequest")) {
                model.addAttribute("checkoutRequest", new CheckoutRequest());
            }
            return "user/checkout";
        } catch (BadRequestException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/checkout")
    public String placeOrder(@ModelAttribute CheckoutRequest checkoutRequest,
                             RedirectAttributes redirectAttributes) {
        try {
            OrderResponse order = orderService.createOrder(checkoutRequest);
            if ("BANK_QR".equals(order.getPaymentMethod())) {
                return "redirect:/orders/" + order.getOrderId() + "/payment";
            }
            return "redirect:/orders/" + order.getOrderId() + "/success";
        } catch (BadRequestException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            redirectAttributes.addFlashAttribute("checkoutRequest", checkoutRequest);
            return "redirect:/checkout";
        }
    }

    @GetMapping("/orders/{orderId}/success")
    public String orderSuccess(@PathVariable Long orderId, Model model) {
        model.addAttribute("home", homeService.getHomeData());
        model.addAttribute("order", orderService.getOrderResponse(orderId));
        return "user/order-success";
    }

    @GetMapping("/orders/{orderId}/payment")
    public String bankQrPayment(@PathVariable Long orderId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            orderService.validateBankQrPaymentPage(orderId);
            model.addAttribute("home", homeService.getHomeData());
            model.addAttribute("order", orderService.getOrderResponse(orderId));
            return "user/payment-qr";
        } catch (BadRequestException | ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/orders/" + orderId + "/success";
        }
    }

    @PostMapping("/orders/{orderId}/confirm-payment")
    public String confirmBankQrPayment(@PathVariable Long orderId,
                                       RedirectAttributes redirectAttributes) {
        try {
            orderService.confirmBankQrPayment(orderId);
            redirectAttributes.addFlashAttribute("successMessage", "Xác nhận thanh toán thành công");
            return "redirect:/orders/" + orderId + "/success";
        } catch (BadRequestException | ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/orders/" + orderId + "/payment";
        }
    }
}
