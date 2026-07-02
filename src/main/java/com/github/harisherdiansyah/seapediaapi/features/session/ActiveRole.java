package com.github.harisherdiansyah.seapediaapi.features.session;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum ActiveRole {
    ADMIN(Set.of("MANAGE_APP", "MANAGE_PROFILE")),
    NON_ADMIN(Set.of("MANAGE_PROFILE", "SELECT_ROLE", "REGISTER_STORE", "REGISTER_DRIVER")),
    BUYER(Set.of("MANAGE_PROFILE", "SELECT_ROLE", "REGISTER_STORE", "REGISTER_DRIVER", "MANAGE_CART")),
    SELLER(Set.of("MANAGE_PROFILE", "SELECT_ROLE", "MANAGE_PRODUCT", "REGISTER_DRIVER")),
    DRIVER(Set.of("MANAGE_PROFILE", "SELECT_ROLE", "REGISTER_STORE"));

    private final Set<String> permissions;

    ActiveRole(Set<String> permissions) {
        this.permissions = permissions;
    }

    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auths = permissions.stream()
                .map(permission -> (GrantedAuthority) new SimpleGrantedAuthority(permission))
                .collect(Collectors.toList());
        auths.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return auths;
    }
}
