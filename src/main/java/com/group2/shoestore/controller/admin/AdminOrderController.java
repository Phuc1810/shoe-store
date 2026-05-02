package com.group2.shoestore.controller.admin;

import com.group2.shoestore.dto.request.OrderStatusUpdateRequest;
import com.group2.shoestore.service.admin.AdminOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller quản lý đơn hàng cho Admin
 * Prefix: /admin/orders
 */
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Slf4j
//@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;
    private static final int PAGE_SIZE = 10;

    /**
     * GET /admin/orders - Hiển thị danh sách đơn hàng với phân trang, tìm kiếm và lọc trạng thái
     */
    @GetMapping
    public String listOrders(
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        
        log.info("Listing orders - page: {}, orderCode: {}, customerName: {}, status: {}", 
                 page, orderCode, customerName, status);
        
        try {
            Pageable pageable = PageRequest.of(page, PAGE_SIZE);
            var orders = adminOrderService.search(orderCode, customerName, status, pageable);
            
            model.addAttribute("orders", orders);
            model.addAttribute("orderCode", orderCode);
            model.addAttribute("customerName", customerName);
            model.addAttribute("status", status);
            
            // Các trạng thái có sẵn để lọc
            model.addAttribute("statuses", new String[]{"CONFIRMED", "SHIPPING", "DELIVERED", "CANCELLED"});
            
            return "admin/order/list";
        } catch (Exception e) {
            log.error("Error listing orders", e);
            model.addAttribute("error", "Có lỗi khi tải danh sách đơn hàng");
            return "admin/order/list";
        }
    }

    /**
     * GET /admin/orders/{id} - Xem chi tiết đơn hàng
     */
    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model) {
        log.info("Viewing order detail for id: {}", id);
        
        try {
            var order = adminOrderService.getOrderById(id);
            model.addAttribute("order", order);
            
            // Các trạng thái có thể chuyển tới
            model.addAttribute("availableStatuses", new String[]{"CONFIRMED", "SHIPPING", "DELIVERED", "CANCELLED"});
            
            return "admin/order/detail";
        } catch (Exception e) {
            log.error("Error viewing order detail", e);
            return "redirect:/admin/orders";
        }
    }

    /**
     * PATCH /admin/orders/{id}/status - Cập nhật trạng thái đơn hàng
     */
    //@PatchMapping("/{id}/status")
    @PostMapping("/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        
        log.info("Updating order status - id: {}, status: {}", id, status);
        
        try {
            adminOrderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái đơn hàng thành công!");
            redirectAttributes.addFlashAttribute("alertType", "success");
        } catch (Exception e) {
            log.error("Error updating order status", e);
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertType", "danger");
        }
        
        return "redirect:/admin/orders/" + id;
    }

    /**
     * POST /admin/orders/{id}/cancel - Hủy đơn hàng
     */
    @PostMapping("/{id}/cancel")
    public String cancelOrder(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        log.info("Cancelling order with id: {}", id);
        
        try {
            adminOrderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("message", "Hủy đơn hàng thành công!");
            redirectAttributes.addFlashAttribute("alertType", "success");
        } catch (Exception e) {
            log.error("Error cancelling order", e);
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertType", "danger");
        }
        
        return "redirect:/admin/orders/" + id;
    }
}
