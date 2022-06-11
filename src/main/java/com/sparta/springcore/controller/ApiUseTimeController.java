package com.sparta.springcore.controller;

import com.sparta.springcore.dto.ApiUseTimeDto;
import com.sparta.springcore.model.ApiUseTime;
import com.sparta.springcore.model.UserRoleEnum;
import com.sparta.springcore.repository.ApiUseTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ApiUseTimeController {
    private final ApiUseTimeRepository apiUseTimeRepository;

    @Autowired
    public ApiUseTimeController(ApiUseTimeRepository apiUseTimeRepository) {
        this.apiUseTimeRepository = apiUseTimeRepository;
    }

    // 관리자에게 전체 회원들의 사용시간 보여주기
    @Secured(UserRoleEnum.Authority.ADMIN)
    @GetMapping("/api/use/time")
    public List<ApiUseTimeDto> getAllApiUseTime() {

        List<ApiUseTime> apiUseTimeList = apiUseTimeRepository.findAll();
        List<ApiUseTimeDto> apiUseTimeDtoList = new ArrayList<>();

        // 전체 데이터가 아니라 username과 totaltime만 내려주기 위한 작업. 현업에서도 이렇게 해야함. 민감한 정보가 있을수도 있기 때문에 DTO 사용 필수.
        for (ApiUseTime apiUseTime : apiUseTimeList) {
            ApiUseTimeDto apiUseTimeDto = new ApiUseTimeDto();
            apiUseTimeDto.setUsername(apiUseTime.getUser().getUsername());
            apiUseTimeDto.setTotalTime(apiUseTime.getTotalTime());
            apiUseTimeDtoList.add(apiUseTimeDto);
        }

        return apiUseTimeDtoList;
    }
}