package com.sixthsense.hexastay.config.Security;

import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomAdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String adminEmail) throws UsernameNotFoundException {
        System.out.println("입력된 adminEmail: " + adminEmail);
        Admin admin = adminRepository.findByAdminEmail(adminEmail);



        return new CustomAdminDetails(admin);

    }
}