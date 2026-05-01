package com.group2.shoestore.service.user;

import com.group2.shoestore.dto.response.UserProfileResponse;
import com.group2.shoestore.entity.User;
import com.group2.shoestore.exception.ResourceNotFoundException;
import com.group2.shoestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private static final Long DEMO_USER_ID = 2L;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getDemoUserProfile() {
        User user = userRepository.findById(DEMO_USER_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin người dùng"));

        return UserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
