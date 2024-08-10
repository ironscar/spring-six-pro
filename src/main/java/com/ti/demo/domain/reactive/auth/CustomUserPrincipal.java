package com.ti.demo.domain.reactive.auth;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ti.demo.domain.reactive.CustomUser;

public class CustomUserPrincipal implements UserDetails {

    private String userName;
    private String password;
    private List<String> roles;
    private boolean accountExpired;
    private boolean accountLocked;
    private boolean credentialsExpired;
    private boolean enabled;

    public CustomUserPrincipal(CustomUser user) {
        userName = user.getUserId();
        password = user.getPassword();
        roles = user.getRoles();
        accountExpired = false;
        accountLocked = false;
        credentialsExpired = false;
        enabled = user.isEnabled();
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role)).toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
}
