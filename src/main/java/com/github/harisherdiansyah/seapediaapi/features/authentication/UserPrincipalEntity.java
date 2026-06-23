package com.github.harisherdiansyah.seapediaapi.features.authentication;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@RequiredArgsConstructor
public class UserPrincipalEntity implements UserDetails {
    private final UUID userId;

    /**
     * Stores the user's email, used as the Spring Security login identifier.
     * Maps to {@code UserEntity.email}.
     */
    private final String username;

    /**
     * Display name (actual username), used for UI representation.
     * Maps to {@code UserEntity.username}.
     * May be {@code null} when principal is reconstructed from a JWT token.
     */
    private final String displayName;

    /**
     * Hashed password. May be {@code null} when principal is reconstructed from a JWT token.
     */
    private final String password;

    private final List<GrantedAuthority> authorities;

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities != null ? authorities : List.of();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
