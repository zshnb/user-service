package com.zshnb.userservice.controller;

import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.request.FollowUserRequest;
import com.zshnb.userservice.request.ListFollowUserRequest;
import com.zshnb.userservice.request.UnfollowUserRequest;
import com.zshnb.userservice.serviceImpl.FollowServiceImpl;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FollowController {
    private final FollowServiceImpl followService;

    public FollowController(FollowServiceImpl followService) {
        this.followService = followService;
    }

    @GetMapping("/follow/{userId}/follow-users")
    public ListResponse<User> listFollowUser(@RequestParam(defaultValue = "1") int pageNumber,
                                             @RequestParam(defaultValue = "20") int pageSize,
                                             @PathVariable int userId) {
        ListFollowUserRequest request = new ListFollowUserRequest();
        request.setUserId(userId);
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        return followService.listFollowUser(request);
    }

    @PostMapping("/follow")
    public Response<String> followUser(@RequestBody FollowUserRequest request) {
        return followService.followUser(request);
    }

    @DeleteMapping("/unfollow")
    public Response<String> unfollowUser(@RequestBody UnfollowUserRequest request) {
        return followService.unfollowUser(request);
    }
}