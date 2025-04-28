package com.sixthsense.hexastay.config.Security;

import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("customMemberDetailsService")
@RequiredArgsConstructor
@Log4j2
public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberEmail) throws UsernameNotFoundException {
        log.info("로그인 시도 - 이메일: {}", memberEmail);
        
        Member member = memberRepository.findByMemberEmail(memberEmail);
        if (member == null) {
            log.error("로그인 실패 - 존재하지 않는 이메일: {}", memberEmail);
            throw new UsernameNotFoundException("존재하지 않는 이메일입니다: " + memberEmail);
        }
        
        // 권한 설정
        List<GrantedAuthority> authorities = new ArrayList<>();
        String role = member.getMemberRole();
        if (role == null || role.trim().isEmpty()) {
            role = "USER";  // 기본 권한 설정
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        
        log.info("로그인 성공 - 사용자: {}, 권한: {}", memberEmail, authorities);
        
        return new CustomMemberDetails(member, authorities);
    }
}