package com.group2.shoestore.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 3, max = 150, message = "Tên sản phẩm phải từ 3 đến 150 ký tự")
    private String name;

    @NotNull(message = "Danh mục không được để trống")
    @Positive(message = "ID danh mục phải là số dương")
    private Long categoryId;

    @Positive(message = "ID thương hiệu phải là số dương")
    private Long brandId;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;

    @Size(max = 255, message = "Mô tả ngắn tối đa 255 ký tự")
    private String shortDescription;

    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.01", message = "Giá phải lớn hơn 0")
    private BigDecimal basePrice;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status;
}
