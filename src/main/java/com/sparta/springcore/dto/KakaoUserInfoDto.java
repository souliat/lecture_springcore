package com.sparta.springcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KakaoUserInfoDto {
    private Long id;
    private String nickname;
    private String email;

//    public KakaoUserInfoDto(Long id, String ninkname, String email) {
//        this.id = id;
//        this.ninkname = nickname;
//        this.email = email;
//    }

}
