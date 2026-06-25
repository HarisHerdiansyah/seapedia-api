package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.features.session.ActiveRole;
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
        try {
            UserEntity user = userService.getUserByEmail(email);
            String userRole = user.getRole().toString();
            ActiveRole activeRole = ActiveRole.valueOf(userRole);

            return UserPrincipalEntity.builder()
                    .userId(user.getId())
                    .username(user.getEmail())
                    .displayName(user.getUsername())
                    .userRole(userRole)
                    .password(user.getPasswordHash())
                    .authorities(activeRole.getAuthorities())
                    .build();
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException("User with email " + email + " not found.");
        }
    }
}
