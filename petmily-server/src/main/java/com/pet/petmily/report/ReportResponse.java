package com.pet.petmily.report;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL) //  null 값을 가지는 필드는, JSON 응답에 포함되지 않음
@Getter
@AllArgsConstructor
public class ReportResponse<T> {

    private String status;
    private String message;
    private String ReportedPostTitle;
    private String content;
    private T data;
}