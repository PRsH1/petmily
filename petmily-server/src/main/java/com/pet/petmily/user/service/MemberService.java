package com.pet.petmily.user.service;

import com.pet.petmily.user.dto.MemberSignUpDto;
import com.pet.petmily.user.dto.MemberUpdateDTO;
import com.pet.petmily.user.entity.Member;
import com.pet.petmily.user.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.pet.petmily.user.repository.MemberRepository;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j

public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;




    public void signUp(MemberSignUpDto memberSignUpDto) throws Exception{

        if(memberRepository.findByEmail(memberSignUpDto.getEmail()).isPresent()){

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"BAD_REQUEST:이미 존재하는 이메일입니다.");
        }
        if(memberRepository.findByNickname(memberSignUpDto.getNickname()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"BAD_REQUEST:이미 존재하는 닉네임입니다.");
        }
        if(Objects.isNull(memberSignUpDto.getEmail())||Objects.isNull(memberSignUpDto.getNickname())||Objects.isNull(memberSignUpDto.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"BAD_REQUEST:필수 입력값이 없습니다:email,nickname,password");
        }

        log.info("회원가입 요청");
        Member member= Member.builder()
                .email(memberSignUpDto.getEmail())
                .password(passwordEncoder.encode(memberSignUpDto.getPassword()))
                .nickname(memberSignUpDto.getNickname())
                .role(Role.USER)
                .status(true)
                .socialType(memberSignUpDto.getSocialType())
                .socialId(memberSignUpDto.getSocialId())




                .build();
        //member.passwordEncode(passwordEncoder);
        memberRepository.save(member);


    }


    public String getNickName(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."))
                .getNickname();
    }

    public LocalDateTime getCreateDate(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."))
                .getCreateDate();
    }

    public Long getId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."))
                .getId();
    }

    @Transactional
    public void updateMember(String email, MemberUpdateDTO memberUpdateDTO) throws Exception {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(memberUpdateDTO.getCurrentPassword(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BAD_REQUEST:현재 비밀번호가 일치하지 않습니다.");
        }

        if (memberUpdateDTO.getEmail() != null && !memberUpdateDTO.getEmail().equals(email)) {
            if (memberRepository.findByEmail(memberUpdateDTO.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BAD_REQUEST:이미 존재하는 이메일입니다.");
            }
        }
        if (memberUpdateDTO.getNickname() != null) {
            if (memberRepository.findByNickname(memberUpdateDTO.getNickname()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BAD_REQUEST:이미 존재하는 닉네임입니다.");
            }
        }

        if (memberUpdateDTO.getPassword() != null) {
            memberUpdateDTO.setPassword(passwordEncoder.encode(memberUpdateDTO.getPassword()));
        }
        member.updateMember(memberUpdateDTO);
        member.setLastModifiedDate(LocalDateTime.now());
        memberRepository.save(member);
    }
    @Transactional
    public ResponseEntity checkEmail(String email) throws Exception{
        if(memberRepository.findByEmail(email).isPresent()){
            return new ResponseEntity("이미 존재하는 이메일입니다.",HttpStatus.BAD_REQUEST);
        }
        else{
            return new ResponseEntity("사용 가능한 이메일입니다.",HttpStatus.OK);
        }
    }


    @Transactional
    public ResponseEntity checkNickname(String nickname) {
        if(memberRepository.findByNickname(nickname).isPresent()){
            return new ResponseEntity("이미 존재하는 닉네임입니다.",HttpStatus.BAD_REQUEST);
        }
        else{
            return new ResponseEntity("사용 가능한 닉네임입니다.",HttpStatus.OK);
        }
    }
}