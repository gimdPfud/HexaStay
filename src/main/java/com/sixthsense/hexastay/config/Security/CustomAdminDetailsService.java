package com.sixthsense.hexastay.config.Security;

import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Log4j2
@Service("customAdminDetailsService")
public class CustomAdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String adminEmail) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByAdminEmail(adminEmail);
        
        if (admin == null) {
            log.error("사용자를 찾을 수 없습니다: {}", adminEmail);
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + adminEmail);
        }
        
        if (!"ACTIVE".equals(admin.getAdminActive())) {
            log.error("비활성화된 계정입니다: {}, 상태: {}", adminEmail, admin.getAdminActive());
            throw new UsernameNotFoundException("비활성화된 계정입니다.");
        }
        
        return new CustomAdminDetails(admin);
    }
}