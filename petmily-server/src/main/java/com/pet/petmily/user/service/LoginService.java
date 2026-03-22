package com.pet.petmily.user.service;

import com.pet.petmily.user.entity.Member;
import com.pet.petmily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (loginAttemptService.isBlocked(email)) {
            long remaining = loginAttemptService.getRemainingLockMinutes(email);
            throw new LockedException("로그인 시도 횟수를 초과했습니다. " + remaining + "분 후 다시 시도해주세요.");
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."));

        // 소셜 로그인 사용자(socialType != null)는 이메일 인증 면제
        if (member.getSocialType() == null && !member.isEmailVerified()) {
            throw new DisabledException("이메일 인증이 필요합니다. 가입 시 발송된 인증 메일을 확인해주세요.");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().name())
                .build();
    }
}
