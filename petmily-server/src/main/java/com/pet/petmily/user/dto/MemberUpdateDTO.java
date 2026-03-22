package com.pet.petmily.user.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data   //getter,setter,toString,equals,hashCode,RequiredArgsConstructor
public class MemberUpdateDTO {

    @NotNull(message = "이 필드는 null일 수 없습니다:email")
    private String email;
    @NotNull(message = "이 필드는 null일 수 없습니다:nickname")
    private String nickname;
    @NotNull(message = "이 필드는 null일 수 없습니다:currentPassword")
    private String currentPassword;
    private String password;

    LocalDateTime lastModifiedDate;
}
