package com.github.harisherdiansyah.seapediaapi.features.authentication;

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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userService.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Email is never exist.");
        }

        List<String> role = List.of(user.getRole().toString());
        List<GrantedAuthority> authorities = role
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserPrincipalEntity.UserPrincipalEntityBuilder builder = UserPrincipalEntity.builder()
                .userId(user.getId())
                .username(user.getEmail())
                .authorities(authorities);
        return builder.build();
    }
}
