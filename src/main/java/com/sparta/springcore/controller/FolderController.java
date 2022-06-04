package com.sparta.springcore.controller;

import com.sparta.springcore.dto.FolderRequestDto;
import com.sparta.springcore.model.Folder;
import com.sparta.springcore.model.Product;
import com.sparta.springcore.model.User;
import com.sparta.springcore.security.UserDetailsImpl;
import com.sparta.springcore.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @PostMapping("/api/folders")
    public List<Folder> addFolders(@RequestBody FolderRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<String> folderNames = requestDto.getFolderNames();
        User user = userDetails.getUser();

        List<Folder> folders = folderService.addFolders(folderNames, user);
        return folders;

    }

    @GetMapping("/api/folders")
    public List<Folder> getFolders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return folderService.getFolders(userDetails.getUser());
    }

    // 폴더 별 관심상품 조회
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
        Page<Product> pageProduct = folderService.getProductsInFolder(folderId, page, size, sortBy, isAsc, userDetails.getUser());
        return pageProduct;
    }
}
