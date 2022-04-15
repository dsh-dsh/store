package com.example.store.model.enums;

import com.example.store.utils.Constants;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role {

    ADMIN(Constants.ADMIN_ROLE, Set.of(Permission.WRITE)),
    CASHIER(Constants.CASHIER_ROLE, Set.of(Permission.WRITE)),
    ACCOUNTANT(Constants.ACCOUNTANT_ROLE, Set.of(Permission.WRITE)),
    CUSTOMER(Constants.CUSTOMER_ROLE, Set.of(Permission.READ)),
    SYSTEM(Constants.ADMIN_ROLE, Set.of(Permission.WRITE));

    private final String value;
    private final Set<Permission> permissions;

    Role(String value, Set<Permission> permissions) {
        this.value = value;
        this.permissions = permissions;
    }

    public String getValue() {
        return this.value;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}
