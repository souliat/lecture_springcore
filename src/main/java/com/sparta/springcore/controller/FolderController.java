package com.sparta.springcore.controller;

import com.sparta.springcore.dto.FolderRequestDto;
import com.sparta.springcore.exception.RestApiException;
import com.sparta.springcore.model.Folder;
import com.sparta.springcore.model.Product;
import com.sparta.springcore.model.User;
import com.sparta.springcore.security.UserDetailsImpl;
import com.sparta.springcore.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FolderController {

    private final FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService) {

        this.folderService = folderService;
    }

    // 폴더 선택하여 저장
    @PostMapping("/api/folders")
    public List<Folder> addFolders(@RequestBody FolderRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // 이렇게 원래는 트라이 캐치문으로 에러를 500 > 400에러로 바꿔줘야하는데, @ExceptionHandler를 쓰면 훨씬 간편해진다.
//        try {
//            List<String> folderNames = requestDto.getFolderNames();
//            User user = userDetails.getUser();
//
//            List<Folder> folderList = folderService.addFolders(folderNames, user);
//            return new ResponseEntity(folderList, HttpStatus.OK);
//
//        }catch(IllegalArgumentException e) {
//
//            RestApiException restApiException = new RestApiException();
//            restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
//            restApiException.setErrorMessage(e.getMessage());
//
//            return new ResponseEntity(restApiException, HttpStatus.BAD_REQUEST);
//        }
            List<String> folderNames = requestDto.getFolderNames();
            User user = userDetails.getUser();

            return folderService.addFolders(folderNames, user);

    }

    // 폴더 조회
    @GetMapping("/api/folders")
    public List<Folder> getFolders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return folderService.getFolders(userDetails.getUser());
    }

    // 폴더 별 관심 상품 조회
    @GetMapping("/api/folders/{folderId}/products")
    public Page<Product> getProductsInFolder(
            @PathVariable Long folderId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {

        page = page - 1; // 이걸 안해주면 서버는 page를 1로 인식해서 2번째 페이지를 가져온다. 그래서 content가 안보이는 경우가 생김.
        return folderService.getProductsInFolder(folderId, page, size, sortBy, isAsc, userDetails.getUser());
    }

    // AOP개념을 이용한 예외처리. 컨트롤러마다 try catch로 할 필요도 없고 너무 좋다. 근데 이것보다 글로벌 선언이 더 좋음. RestApiExceptionHandler
//    @ExceptionHandler({ IllegalArgumentException.class })
//    public ResponseEntity handleException(IllegalArgumentException ex) {
//        RestApiException restApiException = new RestApiException();
//        restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
//        restApiException.setErrorMessage(ex.getMessage());
//
//        return new ResponseEntity(
//        // HTTP body
//                restApiException,
//        // HTTP status code
//                HttpStatus.BAD_REQUEST
//        );
//    }




}
