package com.sixthsense.hexastay.config.Security;

import com.sixthsense.hexastay.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Getter
public class CustomMemberDetails implements UserDetails, Principal {

    private final Member member;

    public CustomMemberDetails(Member member) {
        this.member = member;
    }

    @Override
    public String getUsername() {
        return Optional.ofNullable(member)
                .map(Member::getMemberEmail)
                .orElse(null);
    }

    @Override
    public String getPassword() {
        return Optional.ofNullable(member)
                .map(Member::getMemberPassword)
                .orElse(null);
    }

    public String getMemberName() {
        return Optional.ofNullable(member)
                .map(Member::getMemberName)
                .orElse(null);
    }

    public String getMemberRole() {
        return Optional.ofNullable(member)
                .map(Member::getMemberRole)
                .orElse(null);
    }

    public String getMemberPhone() {
        return Optional.ofNullable(member)
                .map(Member::getMemberPhone)
                .orElse(null);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getMemberRole()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return getUsername();  // null-safe 처리됨
    }
}
