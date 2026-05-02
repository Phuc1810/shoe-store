package com.group2.shoestore.security;

import com.group2.shoestore.entity.Role;
import com.group2.shoestore.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = user.getRole();
        if (role == null || role.getName() == null || role.getName().isBlank()) {
            throw new IllegalStateException("Tài khoản chưa được gán quyền trong hệ thống");
        }

        String rawRoleName = role.getName().trim();
        String roleName = rawRoleName.startsWith("ROLE_")
                ? rawRoleName
                : "ROLE_" + rawRoleName.toUpperCase();

        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equalsIgnoreCase(user.getStatus());
    }

    public Long getId() {
        return user.getId();
    }

    public String getFullName() {
        return user.getFullName();
    }

    public String getEmail() {
        return user.getEmail();
    }
}
