package com.sixthsense.hexastay.config.Security;

import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Company;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Getter
public class CustomAdminDetails implements UserDetails, Principal {

    private static final Logger log = LoggerFactory.getLogger(CustomAdminDetails.class);

    private final Admin admin;

    public CustomAdminDetails(Admin admin) {
        this.admin = admin;
    }

    @Override
    public String getUsername() {
        return Optional.ofNullable(admin)
                .map(Admin::getAdminEmail)
                .orElse(null);
    }

    @Override
    public String getPassword() {
        String password = Optional.ofNullable(admin)
                .map(Admin::getAdminPassword)
                .orElse(null);
        log.info("CustomAdminDetails - getPassword 호출됨: {}", password);
        return password;
    }

    public String getAdminPosition() {
        return Optional.ofNullable(admin)
                .map(Admin::getAdminPosition)
                .orElse(null);
    }

    public String getAdminName() {
        return Optional.ofNullable(admin)
                .map(Admin::getAdminName)
                .orElse(null);
    }

    public String getAdminRole() {
        return Optional.ofNullable(admin)
                .map(Admin::getAdminRole)
                .orElse(null);
    }

    public String getAdminEmployeeNum() {
        return Optional.ofNullable(admin)
                .map(Admin::getAdminEmployeeNum)
                .orElse(null);
    }

    public Long getCompanyNum() {
        return Optional.ofNullable(admin)
                .map(Admin::getCompany)
                .map(Company::getCompanyNum)
                .orElse(null);
    }


    public String getAdminProfileMeta() {
        return Optional.ofNullable(admin)
                .map(Admin::getAdminProfileMeta)
                .orElse(null);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getAdminRole()));
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
        return "ACTIVE".equals(admin.getAdminActive());
    }

    @Override
    public String getName() {
        return getUsername();
    }
}
