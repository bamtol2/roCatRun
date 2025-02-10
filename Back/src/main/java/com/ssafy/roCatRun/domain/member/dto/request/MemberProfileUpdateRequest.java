package com.ssafy.roCatRun.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberProfileUpdateRequest {
    private Integer height;
    private Integer weight;
    private Integer age;
    private String gender;
}
