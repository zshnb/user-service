package com.zshnb.userservice.controller;

import static com.zshnb.userservice.common.UserConstant.SERVER_URL;

import com.ejlchina.okhttps.OkHttps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    private final FollowServiceImpl followService;

    public FollowController(FollowServiceImpl followService) {
        this.followService = followService;
    }

    @GetMapping("/follow/{userId}/follow-users")
    public ListResponse<User> listFollowUser(@RequestParam(defaultValue = "1") int pageNumber,
                                             @RequestParam(defaultValue = "20") int pageSize,
                                             @RequestParam(defaultValue = "") String code,
                                             @RequestParam(name = "client_id", defaultValue = "") String clientId,
                                             @RequestParam(name = "secret", defaultValue = "") String clientSecret,
                                             @PathVariable int userId) {
        String str = OkHttps.sync(SERVER_URL + "/oauth2/token")
            .addUrlPara("grant_type", "authorization_code")
            .addUrlPara("code", code)
            .addUrlPara("client_id", clientId)
            .addUrlPara("client_secret", clientSecret)
            .get()
            .getBody()
            .toString();
        JsonObject jsonObject = new Gson().fromJson(str, JsonObject.class);
        if(jsonObject == null || jsonObject.get("code").getAsInt() != 200) {
            throw new PermissionDenyException();
        }

        JsonObject data = jsonObject.get("data").getAsJsonObject();
        String accessToken = data.get("access_token").getAsString();
        str = OkHttps.sync(SERVER_URL + "/api/oauth2/user-info")
            .addUrlPara("access_token", accessToken)
            .get()
            .getBody()
            .toString();
        jsonObject = new Gson().fromJson(str, JsonObject.class);
        if(jsonObject.get("code").getAsInt() != 200) {
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
