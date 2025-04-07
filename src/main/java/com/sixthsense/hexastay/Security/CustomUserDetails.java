//package com.sixthsense.hexastay.Security;
//
//import com.example.pz1teamganttchart.entity.Member;
//import com.sixthsense.hexastay.entity.Admin;
//import lombok.Getter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.List;
//
//@Getter
//public class CustomUserDetails implements UserDetails {
//
//    private Admin admin;
//
//    @Override
//    public String getUsername() {
//        return admin.getAdminEmail();
//    }
//
//    @Override
//    public String getPassword() {
//        return admin.getAdminPassword();
//    }
//
//    @Override
//    public String getAdminPosition(){
//        return admin.getAdminPosition();
//}
//
//    @Override
//    public String getAdminName(){
//        return admin.getAdminName();
//    }
//
//    @Override
//    public Integer getAdminEmployeeNum(){
//        return admin.getAdminEmployeeNum();
//    }
//
//    @Override
//    public String getAdminBrand(){
//        return admin.getAdminBrand();
//    }
//
//    @Override String getAdminPofile(){
//        return admin.getAdminProfile();
//    }
//
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("ROLE_" + admin.getAdminRole()));
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
