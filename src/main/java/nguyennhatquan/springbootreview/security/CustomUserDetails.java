package nguyennhatquan.springbootreview.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import nguyennhatquan.springbootreview.entity.Cart;
import nguyennhatquan.springbootreview.entity.Order;
import nguyennhatquan.springbootreview.entity.Role;
import nguyennhatquan.springbootreview.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String address;
    private Boolean isActive;
    private Role role;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Cart cart;
    private List<Order> orders;

    /**
     * Create CustomUserDetails from User entity
     */
    public static CustomUserDetails fromUser(User user) {
        return CustomUserDetails.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .isActive(user.getIsActive())
                .role(user.getRole())
                .version(user.getVersion())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .cart(user.getCart())
                .orders(user.getOrders())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null || role.getName() == null) {
            return List.of();
        }
        // role name should be stored without "ROLE_" prefix in DB; add prefix for Spring Security
        String roleName = role.getName();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(isActive);
    }
}
