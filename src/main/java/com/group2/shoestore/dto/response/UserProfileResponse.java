package com.group2.shoestore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long userId;

    private String username;

    private String fullName;

    private String email;

    private String phone;

    private String roleName;

    private String status;

    private LocalDateTime createdAt;
}
