package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.features.users.UserEntity;
import com.github.harisherdiansyah.seapediaapi.features.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPrincipalService implements UserDetailsService {
    private final UserService userService;

    /**
     * Loads user by email. Spring Security uses "username" generically,
     * but in this system the login identifier is the email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserEntity user = userService.getUserByEmail(email);

            List<String> role = List.of(user.getRole().toString());
            List<GrantedAuthority> authorities = role
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return UserPrincipalEntity.builder()
                    .userId(user.getId())
                    .username(user.getEmail())
                    .displayName(user.getUsername())
                    .password(user.getPasswordHash())
                    .authorities(authorities)
                    .build();
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException("User with email " + email + " not found.");
        }
    }
}
