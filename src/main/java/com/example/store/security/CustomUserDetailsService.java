package com.example.store.security;

import com.example.store.model.entities.User;
import com.example.store.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetails setUserDetails(User user) {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("USER");
        return CustomUserDetails.builder()
                .userName(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByEmail(username);
        return user == null ? null : setUserDetails(user);
    }
}
