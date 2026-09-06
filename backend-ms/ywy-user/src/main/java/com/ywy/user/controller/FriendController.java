package com.ywy.user.controller;

import com.ywy.common.result.R;
import com.ywy.common.utils.UserContext;
import com.ywy.user.domain.po.FriendShip;
import com.ywy.user.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public R<List<FriendShip>> list() {
        return R.ok(friendService.list(UserContext.uid()));
    }

    @PostMapping("/apply")
    public R<Void> apply(@RequestParam Long friendId) {
        friendService.apply(UserContext.uid(), friendId);
        return R.ok();
    }

    @PostMapping("/accept")
    public R<Void> accept(@RequestParam Long friendId) {
        friendService.accept(UserContext.uid(), friendId);
        return R.ok();
    }

    @DeleteMapping("/{friendId}")
    public R<Void> remove(@PathVariable Long friendId) {
        friendService.remove(UserContext.uid(), friendId);
        return R.ok();
    }
}