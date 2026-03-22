package com.pet.petmily.user.controller;

import com.pet.petmily.user.dto.MemberLoginDTO;
import com.pet.petmily.user.dto.MemberSignUpDto;
import com.pet.petmily.user.dto.MemberUpdateDTO;
import com.pet.petmily.user.entity.BaseTimeEntity;
import com.pet.petmily.user.entity.Member;
import com.pet.petmily.user.handler.LoginSuccessHandler;
import com.pet.petmily.user.service.LoginService;
import com.pet.petmily.user.service.MemberService;

import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.time.LocalDateTime;


@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController extends BaseTimeEntity {


    private final MemberService memberService;




    @ApiOperation(value = "회원가입", notes = "회원가입")
    @PostMapping("/sign-up")
    public String signUp(@RequestBody MemberSignUpDto memberSignUpDto) throws Exception {
        memberService.signUp(memberSignUpDto);

        return "회원가입 성공";
    }
    @ApiOperation(value = "이메일 체크", notes = "이메일 체크")
    @PostMapping("sign-up/email-check")
    public ResponseEntity emailCheck(@RequestBody MemberSignUpDto memberSignUpDto) throws Exception {
        String email=memberSignUpDto.getEmail();
        return memberService.checkEmail(email);
    }
    @ApiOperation(value = "닉네임 체크", notes = "닉네임 체크")
    @PostMapping("sign-up/nickname-check")
    public ResponseEntity nicknameCheck(@RequestBody MemberSignUpDto memberSignUpDto) throws Exception {
        String nickname=memberSignUpDto.getNickname();
        return memberService.checkNickname(nickname);
    }

    @ApiOperation(value = "유저 정보", notes = "유저 정보")
    @GetMapping("/user/info")
    @ResponseBody
    public Object userInfo(Principal principal,MemberLoginDTO memberLoginDTO){
        String email=principal.getName();
        String nickName=memberService.getNickName(email);
        LocalDateTime createDate=memberService.getCreateDate(email);
        memberLoginDTO.setEmail(email);
        memberLoginDTO.setNickname(nickName);
        memberLoginDTO.setPassword("비밀번호는 보안상 알 수 없습니다.");
        memberLoginDTO.setCreateDate(createDate);

        return memberLoginDTO;

    }

    @ApiOperation(value = "유저 정보 업데이트", notes = "유저 정보 업데이트")
    @PatchMapping("/user/update")
    //유저 정보 업데이트(수정)
    public String update(Principal principal,@RequestBody MemberUpdateDTO memberUpdateDTO) throws Exception {
        log.info("업데이트 요청");
        String email=principal.getName();

        memberService.updateMember(email,memberUpdateDTO);

        return "업데이트 성공";
    }




    











    @ApiOperation(value = "jwt-test", notes = "jwt-test")
    @GetMapping("/jwt-test")
    public String jwtTest() {

        return "jwtTest 요청 성공";
    }
}
