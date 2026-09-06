package com.ywy.user.controller;

import com.ywy.api.dto.user.UserInfoDTO;
import com.ywy.common.result.R;
import com.ywy.common.utils.UserContext;
import com.ywy.user.dto.ProfileDTO;
import com.ywy.user.dto.ProfileUpdateRequest;
import com.ywy.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public R<ProfileDTO> me() {
        return R.ok(userService.getProfile(UserContext.uid()));
    }

    @PutMapping("/me")
    public R<ProfileDTO> update(@Valid @RequestBody ProfileUpdateRequest req) {
        return R.ok(userService.updateProfile(UserContext.uid(), req));
    }

    /** 跨服务读取：Feign UserClient 直连本接口，不经网关，返回裸对象便于反序列化。 */
    @GetMapping("/{id}")
    public UserInfoDTO getById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}