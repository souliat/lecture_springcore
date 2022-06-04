package com.sparta.springcore.service;

import com.sparta.springcore.dto.ProductMypriceRequestDto;
import com.sparta.springcore.dto.ProductRequestDto;
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

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final FolderRepository folderRepository;
    public static final int MIN_MY_PRICE = 100;

    @Autowired
    public ProductService(ProductRepository productRepository, FolderRepository folderRepository) {
        this.productRepository = productRepository;
        this.folderRepository = folderRepository;
    }

    // 관심상품 등록하기
    public Product createProduct(ProductRequestDto requestDto, Long userId ) {
        // 요청받은 DTO 로 DB에 저장할 객체 만들기
        Product product = new Product(requestDto, userId);
        productRepository.save(product);

        return product;
    }

    //최저가격 설정하기
    public Product updateProduct(Long id, ProductMypriceRequestDto requestDto) {
        int myprice = requestDto.getMyprice();
        if (myprice < MIN_MY_PRICE) {
            throw new IllegalArgumentException("유효하지 않은 관심 가격입니다. 최소 " + MIN_MY_PRICE + " 원 이상으로 설정해 주세요.");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("해당 아이디가 존재하지 않습니다."));

        product.setMyprice(myprice);
        productRepository.save(product);

        return product;
    }

    // 회원 ID 로 등록된 상품 조회
    public Page<Product> getProducts(Long userId, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 조회할 때 폴더리스트를 들고 오려면 뭔가 여기서 folderRepository를 이용해서 List<Folder> 를 가져오고
        // product에 넣어주어야 하지 않을까 라는 생각을 할 수 있다.
        // 근데 여기서는 이미 Product Class에 folderList가 ManyToMany로 설정되어있기 때문에
        // 아무것도 안해주어도 Product에 folderList가 잘 들어온다. 신기함.
        // 여기서 바로 Product에 저장되는 것은 아니고, Controller에서 Json 데이터로 변환될 때 그때 folderList가 추가된다고 함.
        // fetch 개념, 추가 공부 필요할듯?

        return productRepository.findAllByUserId(userId, pageable);
    }

    // (관리자용) 상품 전체 조회
    public Page<Product> getAllProducts(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findAll(pageable);
    }


    // 관심 상품에 폴더 추가하기
    @Transactional
    public Product addFolders(Long productId, Long folderId, User user) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException("해당 아이디가 존재하지 않습니다."));

        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new NullPointerException("해당 아이디가 존재하지 않습니다."));

        Long loginUserId = user.getId();
        if (!product.getUserId().equals(loginUserId) || !folder.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("회원님의 관심상품이 아니거나, 회원님의 폴더가 아닙니다.");
        }

        product.addFolder(folder);
        // 이렇게 1차 캐시를 DB에 저장하는 방법이 있고, Transactional을 활용하는 방법이 있다.
//        productRepository.save(product);
        return product;
    }
}