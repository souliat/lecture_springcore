package com.sparta.springcore.model;

import com.sparta.springcore.dto.ProductRequestDto;
import com.sparta.springcore.validator.ProductValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Setter
@Getter // get 함수를 일괄적으로 만들어줍니다.
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@Entity // DB 테이블 역할을 합니다.
public class Product {

    // ID가 자동으로 생성 및 증가합니다.
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    // 반드시 값을 가지도록 합니다.
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private int lprice;

    @Column(nullable = false)
    private int myprice;

    @Column(nullable = false)
    private Long userId;

    @ManyToMany // fetch 라는 개념을 공부해보자.
    private List<Folder> folderList;

    // 관심 상품 생성 시 이용합니다.
    public Product(ProductRequestDto requestDto, Long userId) {

        // 입력값 Validation
//        ProductValidator productValidator = new ProductValidator();
//        productValidator.validateProductInput(requestDto, userId);
        // 근데 이 방식은 Product가 Bean으로 등록이 안되어 있기 때문에 DI가 어려움.
        // 의도한 Bean DI 방식이 아니므로, 비효율적 -> 따라서 validateProductInput 함수에 static을 선언해서 좀 더 간편하게 씀.
        // 객체 생성 없이도 함수를 바로 불러올 수 있는 static의 개념.

        ProductValidator.validateProductInput(requestDto, userId);

        // 관심상품을 등록한 회원 Id 저장
        this.userId = userId;
        this.title = requestDto.getTitle();
        this.image = requestDto.getImage();
        this.link = requestDto.getLink();
        this.lprice = requestDto.getLprice();
        this.myprice = 0;
    }


    public void addFolder(Folder folder) {
        this.folderList.add(folder);
    }
}