package com.sparta.springcore.service;

import com.sparta.springcore.model.Folder;
import com.sparta.springcore.model.Product;
import com.sparta.springcore.model.User;
import com.sparta.springcore.repository.FolderRepository;
import com.sparta.springcore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public FolderService(
            FolderRepository folderRepository,
            ProductRepository productRepository
    ) {
        this.folderRepository = folderRepository;
        this.productRepository = productRepository;
    }

    // 로그인한 회원에 폴더들 등록(5-10 강의 기준)
    @Transactional
    public List<Folder> addFolders(List<String> folderNames, User user) {
        List<Folder> savedFolderList = new ArrayList<>();
        for (String folderName : folderNames) {
            Folder folder = createFolderOrThrow(folderName, user);
            savedFolderList.add(folder);
        }

        return savedFolderList;
    }
//    @Transactional  여기에 이렇게 Transactional을 하면 boolean부터 return까지 정상적으로 동작하는(중복되지 않은) 중심 과정이 다 끝나면 commit 해버림.
    // 그래서 rollback을 할 수 없고 정상적인 폴더명은 저장이 되는 것.
    public Folder createFolderOrThrow(String folderName, User user) {
        // 입력으로 들어온 폴더 이름이 이미 존재하는 경우, Exception 발생
        boolean isExistFolder = folderRepository.existsByUserAndName(user, folderName);

        if (isExistFolder) {
            throw new IllegalArgumentException("중복된 폴더명을 제거해 주세요! 폴더명: " + folderName);
            // try catch가 없기떄문에 여기서 에러 발생하면 타고타고 올라가서 결국 컨트롤러에서 에러가 터짐. 그래서 500에러로 뜨는 것.
            // 하지만 원래는 이 에러가 400에러로 떠야하는 것임. 클라이언트쪽에서 중복된 폴더명을 입력했기때문이므로.. 처리해주자.
        }

        // 폴더명 저장
        Folder folder = new Folder(folderName, user);
        return folderRepository.save(folder);
    }


//    // 로그인한 회원에 폴더들 등록(5-9 강의 기준)
//    @Transactional // 이것이 해결책 2. @Transactional 어노테이션.
//    public List<Folder> addFolders(List<String> folderNames, User user) {
//        // 1) 입력으로 들어온 폴더 이름을 기준으로, 회원이 이미 생성한 폴더들을 조회합니다.
//        List<Folder> existFolderList = folderRepository.findAllByUserAndNameIn(user, folderNames);
//        List<Folder> savedFolderList = new ArrayList<>();
//
//        for (String folderName : folderNames) {
//            // 2) 이미 생성한 폴더가 아닌 경우만 폴더 생성
//            if (isExistFolderName(folderName, existFolderList)) {
//                // 이렇게 써주는 것이 해결책 1. DB에 저장됐던 savedFolderList를 삭제해주고 에러를 발생시키는 것.
////                folderRepository.deleteAll(savedFolderList);
//                throw new IllegalArgumentException("중복된 폴더명을 제거해주세요. 폴더명 : " + folderName);
//            }else {
//                Folder folder = new Folder(folderName, user);
//                folderRepository.save(folder);
//                savedFolderList.add(folder);
//            }
//        }
//
//        return savedFolderList;
//    }

    // 로그인한 회원이 등록된 모든 폴더 조회
    public List<Folder> getFolders(User user) {
        return folderRepository.findAllByUser(user);
    }

    // 회원 ID 가 소유한 폴더에 저장되어 있는 상품들 조회
    public Page<Product> getProductsInFolder(
            Long folderId,
            int page,
            int size,
            String sortBy,
            boolean isAsc,
            User user
    ) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Long userId = user.getId();
        return productRepository.findAllByUserIdAndFolderList_Id(userId, folderId, pageable);
    }
}