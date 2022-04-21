package com.example.store.security;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.User;
import com.example.store.repositories.UserRepository;
import com.example.store.services.UserService;
import com.example.store.utils.Constants;
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

    private final UserRepository userRepository;

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
        User user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_USER_MESSAGE));
        return user == null ? null : setUserDetails(user);
    }
}
