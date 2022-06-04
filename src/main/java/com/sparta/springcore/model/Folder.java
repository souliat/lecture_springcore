package com.sparta.springcore.model;

import com.sparta.springcore.dto.ProductRequestDto;
import com.sparta.springcore.validator.ProductValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter // get 함수를 일괄적으로 만들어줍니다.
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@Entity // DB 테이블 역할을 합니다.
public class Folder {

    // ID가 자동으로 생성 및 증가합니다.
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne // User와의 연관관계. 1명의 User는 N개의 폴더를 가질 수 있기 때문에. 폴더입장에서는 Many to One이 되는 것.
    @JoinColumn(name = "USER_ID", nullable = false) // 외래키로 설정. User의 ID 범위 내에 있는 USER_ID 값만 DB에 저장할 수 있음. 범위를 벗어나면 DB에서 에러를 띄움.
    private User user;

    // 관심 상품 생성 시 이용합니다.
    public Folder(String name, User user) {

        this.name = name;
        this.user = user;
    }


}