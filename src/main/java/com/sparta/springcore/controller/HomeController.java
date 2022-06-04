package com.sparta.springcore.controller;

import com.sparta.springcore.model.Folder;
import com.sparta.springcore.model.UserRoleEnum;
import com.sparta.springcore.repository.FolderRepository;
import com.sparta.springcore.security.UserDetailsImpl;
import com.sparta.springcore.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final FolderService folderService;

//    @Autowired  // 이렇게 해도 되지만 역할을 지켜서 service로 받고 함수를 만들어서 진행해보자.
//    public HomeController(FolderRepository folderRepository) {
//        this.folderRepository = folderRepository;
//    }

    @Autowired
    public HomeController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("username", userDetails.getUsername());

        if (userDetails.getUser().getRole() == UserRoleEnum.ADMIN) {
            model.addAttribute("admin_role", true);
        }

        List<Folder> folderList = folderService.getFolders(userDetails.getUser());
        model.addAttribute("folders", folderList);

        return "index";
    }
}
