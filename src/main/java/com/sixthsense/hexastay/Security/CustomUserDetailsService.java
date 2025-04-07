//package com.sixthsense.hexastay.Security;
//
//import com.example.pz1teamganttchart.entity.Member;
//import com.example.pz1teamganttchart.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Log4j2
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final MemberRepository memberRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
//        System.out.println("입력된 memberId: " + memberId);
//        Member member = memberRepository.findByMemberId(memberId);
//
//        return new CustomUserDetails(member);
//    }
//}