package com.pet.petmily.user.dto;

import com.pet.petmily.user.entity.BaseTimeEntity;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDateTime;

@Data
public class MemberLoginDTO {
    private String email;

    @JsonIgnore
    private String password;

    private String nickname;
    LocalDateTime createDate;
}
