package com.group2.shoestore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {

    private String receiverName;

    private String receiverPhone;

    private String shippingAddress;

    private String note;

    private String paymentMethod;
}
