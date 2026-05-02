package com.group2.shoestore.service.user;

import com.group2.shoestore.dto.request.RegisterRequest;
import com.group2.shoestore.entity.Role;
import com.group2.shoestore.entity.User;
import com.group2.shoestore.exception.BadRequestException;
import com.group2.shoestore.repository.RoleRepository;
import com.group2.shoestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequest request) {
        if (request == null) {
            throw new BadRequestException("Thông tin đăng ký không hợp lệ");
        }

        String username = normalize(request.getUsername());
        String fullName = normalize(request.getFullName());
        String email = normalize(request.getEmail());
        String phone = normalize(request.getPhone());
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        if (username == null || fullName == null || email == null || phone == null || password == null || confirmPassword == null) {
            throw new BadRequestException("Vui lòng nhập đầy đủ thông tin đăng ký");
        }
        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp");
        }
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Tên đăng nhập đã được sử dụng");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email đã được sử dụng");
        }

        Role userRole = roleRepository.findByName("USER")
                .or(() -> roleRepository.findByName("CUSTOMER"))
                .orElseThrow(() -> new BadRequestException("Không tìm thấy quyền USER trong hệ thống"));

        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .username(username)
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .passwordHash(passwordEncoder.encode(password))
                .role(userRole)
                .status(ACTIVE_STATUS)
                .createdAt(now)
                .updatedAt(now)
                .build();

        userRepository.save(user);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
