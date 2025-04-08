package com.sixthsense.hexastay.config.Security;

import com.sixthsense.hexastay.entity.Admin;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private Admin admin;

    public CustomUserDetails(Admin admin) {
        this.admin = admin;
    }

    @Override
    public String getUsername() {
        return admin.getAdminEmail();
    }

    @Override
    public String getPassword() {
        return admin.getAdminPassword();
    }

    public String getAdminPosition(){
        return admin.getAdminPosition();
}

    public String getAdminName(){
        return admin.getAdminName();
    }

    public Integer getAdminEmployeeNum(){
        return admin.getAdminEmployeeNum();
    }

    public String getAdminBrand(){
        return admin.getAdminBrand();
    }

    public String getAdminProfileMeta(){
        return admin.getAdminProfileMeta();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + admin.getAdminRole()));
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
}
