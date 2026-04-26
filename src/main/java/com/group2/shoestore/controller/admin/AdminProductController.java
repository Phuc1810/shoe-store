package com.group2.shoestore.controller.admin;

import com.group2.shoestore.dto.request.ProductRequest;
import com.group2.shoestore.dto.response.ProductResponse;
import com.group2.shoestore.repository.BrandRepository;
import com.group2.shoestore.repository.CategoryRepository;
import com.group2.shoestore.service.admin.AdminProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Slf4j
//@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    private static final int PAGE_SIZE = 10;


    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        log.info("Listing products - page: {}, name: {}, categoryId: {}, brandId: {}, status: {}",
                page, name, categoryId, brandId, status);

        try {
            Pageable pageable = PageRequest.of(page, PAGE_SIZE);
            var products = adminProductService.search(name, categoryId, brandId, status, pageable);

            model.addAttribute("products", products);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            model.addAttribute("name", name);
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("brandId", brandId);
            model.addAttribute("status", status);

            return "admin/products";
        } catch (Exception e) {
            log.error("Error listing products", e);
            model.addAttribute("error", "Có lỗi khi tải danh sách sản phẩm");
            return "admin/products";
        }
    }


    @GetMapping("/add")
    public String showAddForm(Model model) {
        log.info("Showing add product form");

        try {
            model.addAttribute("product", new ProductRequest());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            model.addAttribute("genders", new String[]{"MEN", "WOMEN", "UNISEX", "KIDS"});
            model.addAttribute("statuses", new String[]{"ACTIVE", "INACTIVE"});
            model.addAttribute("isEdit", false);

            return "admin/product-form";
        } catch (Exception e) {
            log.error("Error showing add form", e);
            return "redirect:/admin/products";
        }
    }


    @PostMapping
    public String createProduct(
            @Valid @ModelAttribute("product") ProductRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Creating new product: {}", request.getName());


        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for product creation");
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            model.addAttribute("genders", new String[]{"MEN", "WOMEN", "UNISEX", "KIDS"});
            model.addAttribute("statuses", new String[]{"ACTIVE", "INACTIVE"});
            model.addAttribute("isEdit", false);
            return "admin/product-form";
        }

        try {
            ProductResponse response = adminProductService.createProduct(request);
            redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm thành công!");
            redirectAttributes.addFlashAttribute("alertType", "success");
            return "redirect:/admin/products";
        } catch (Exception e) {
            log.error("Error creating product", e);
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertType", "danger");
            return "redirect:/admin/products/add";
        }
    }


    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        log.info("Showing edit form for product id: {}", id);

        try {
            ProductResponse product = adminProductService.getProductById(id);

            ProductRequest request = new ProductRequest();
            request.setName(product.getName());
            request.setCategoryId(product.getCategoryId());
            request.setBrandId(product.getBrandId());
            request.setGender(product.getGender());
            request.setShortDescription(product.getShortDescription());
            request.setDescription(product.getDescription());
            request.setBasePrice(product.getBasePrice());
            request.setStatus(product.getStatus());

            model.addAttribute("product", request);
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            model.addAttribute("genders", new String[]{"MEN", "WOMEN", "UNISEX", "KIDS"});
            model.addAttribute("statuses", new String[]{"ACTIVE", "INACTIVE"});
            model.addAttribute("isEdit", true);

            return "admin/product-form";
        } catch (Exception e) {
            log.error("Error showing edit form", e);
            return "redirect:/admin/products";
        }
    }


    @PutMapping("/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("product") ProductRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Updating product with id: {}", id);

        // Kiểm tra validation
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for product update");
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            model.addAttribute("genders", new String[]{"MEN", "WOMEN", "UNISEX", "KIDS"});
            model.addAttribute("statuses", new String[]{"ACTIVE", "INACTIVE"});
            model.addAttribute("isEdit", true);
            return "admin/product-form";
        }

        try {
            adminProductService.updateProduct(id, request);
            redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công!");
            redirectAttributes.addFlashAttribute("alertType", "success");
            return "redirect:/admin/products";
        } catch (Exception e) {
            log.error("Error updating product", e);
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertType", "danger");
            return "redirect:/admin/products/" + id + "/edit";
        }
    }


    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Deleting product with id: {}", id);

        try {
            adminProductService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thành công!");
            redirectAttributes.addFlashAttribute("alertType", "success");
        } catch (Exception e) {
            log.error("Error deleting product", e);
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertType", "danger");
        }

        return "redirect:/admin/products";
    }


    @GetMapping("/{id}")
    public String viewProductDetail(@PathVariable Long id, Model model) {
        log.info("Viewing product detail for id: {}", id);

        try {
            ProductResponse product = adminProductService.getProductById(id);
            model.addAttribute("product", product);
            return "admin/product-detail";
        } catch (Exception e) {
            log.error("Error viewing product detail", e);
            return "redirect:/admin/products";
        }
    }
}
