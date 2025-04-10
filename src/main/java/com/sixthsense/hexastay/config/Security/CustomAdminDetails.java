package com.sixthsense.hexastay.config.Security;

import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Branch;
import com.sixthsense.hexastay.entity.Center;
import com.sixthsense.hexastay.entity.Facility;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Getter
public class CustomAdminDetails implements UserDetails, Principal {

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
        return Optional.ofNullable(admin)
                .map(Admin::getAdminPassword)
                .orElse(null);
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

    public Long getCenterNum() {
        return Optional.ofNullable(admin)
                .map(Admin::getCenter)
                .map(Center::getCenterNum)
                .orElse(null);
    }

    public Long getBranchNum() {
        return Optional.ofNullable(admin)
                .map(Admin::getBranch)
                .map(Branch::getBranchNum)
                .orElse(null);
    }

    public Long getFacilityNum() {
        return Optional.ofNullable(admin)
                .map(Admin::getFacility)
                .map(Facility::getFacilityNum)
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
        return true;
    }

    @Override
    public String getName() {
        return getUsername();
    }
}
