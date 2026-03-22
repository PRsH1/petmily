package com.pet.petmily.user.dto;

import com.pet.petmily.user.entity.BaseTimeEntity;

import com.pet.petmily.user.entity.SocialType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class MemberSignUpDto extends BaseTimeEntity{




    @NotNull(message = "이 필드는 null일 수 없습니다:email")  //null이면 안되는 값
    private String email;
    @NotNull(message = "이 필드는 null일 수 없습니다:password") //null이면 안되는 값
    private String password;

    @NotNull (message = "이 필드는 null일 수 없습니다:nickname")   //null이면 안되는 값
    private String nickname;

    private boolean status;
    private SocialType socialType;
    private String socialId;




}