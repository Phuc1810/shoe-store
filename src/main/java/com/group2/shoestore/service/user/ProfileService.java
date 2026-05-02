package com.group2.shoestore.service.user;

import com.group2.shoestore.dto.response.UserProfileResponse;
import com.group2.shoestore.entity.User;
import com.group2.shoestore.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        User user = currentUserService.getCurrentUser();

        return UserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .status(resolveStatusText(user.getStatus()))
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String resolveStatusText(String status) {
        if ("ACTIVE".equalsIgnoreCase(status)) {
            return "Đang hoạt động";
        }
        if ("INACTIVE".equalsIgnoreCase(status)) {
            return "Ngưng hoạt động";
        }
        return status;
    }
}
