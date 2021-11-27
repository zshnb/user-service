package com.zshnb.userservice.controller;

import com.ejlchina.okhttps.OkHttps;
import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.exception.PermissionDenyException;
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
    private final String serverUrl = "http://localhost:8080";
    private final FollowServiceImpl followService;

    public FollowController(FollowServiceImpl followService) {
        this.followService = followService;
    }

    @GetMapping("/follow/{userId}/follow-users")
    public ListResponse<User> listFollowUser(@RequestParam(defaultValue = "1") int pageNumber,
                                             @RequestParam(defaultValue = "20") int pageSize,
                                             @RequestParam String code,
                                             @RequestParam String clientSecret,
                                             @PathVariable int userId) {
        String str = OkHttps.sync(serverUrl + "/oauth2/token")
            .addBodyPara("grant_type", "authorization_code")
            .addBodyPara("code", code)
            .addBodyPara("client_id", userId)
            .addBodyPara("client_secret", clientSecret)
            .post()
            .getBody()
            .toString();

        SoMap so = SoMap.getSoMap().setJsonString(str);
        System.out.println("返回结果: " + so);

        // code不等于200  代表请求失败
        if(so.getInt("code") != 200) {
            throw new PermissionDenyException();
        }

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
