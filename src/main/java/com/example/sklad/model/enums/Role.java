package com.example.sklad.model.enums;

import com.example.sklad.utils.Constants;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role {

    ADMIN(Constants.ADMIN_ROLE, Set.of(Permission.WRITE)),
    CASHIER(Constants.CASHIER_ROLE, Set.of(Permission.WRITE)),
    ACCOUNTANT(Constants.ACCOUNTANT_ROLE, Set.of(Permission.WRITE));

    private final String name;
    private final Set<Permission> permissions;

    Role(String name, Set<Permission> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return this.name;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}
