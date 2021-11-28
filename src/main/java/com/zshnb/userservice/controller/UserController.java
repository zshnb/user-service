package com.zshnb.userservice.controller;

import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.request.AddUserRequest;
import com.zshnb.userservice.request.UpdateUserRequest;
import com.zshnb.userservice.serviceImpl.UserServiceImpl;
import com.zshnb.userservice.util.OAuth2Util;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserServiceImpl userService;
    private final OAuth2Util oAuth2Util;

    public UserController(UserServiceImpl userService, OAuth2Util oAuth2Util) {
        this.userService = userService;
        this.oAuth2Util = oAuth2Util;
    }

    @PostMapping
    public Response<User> add(@RequestBody AddUserRequest request) {
        User user = userService.add(request);
        return Response.ok(user);
    }

    @PutMapping("/{id}")
    public Response<User> update(@PathVariable int id, @RequestBody UpdateUserRequest request) {
        User user = userService.update(id, request);
        return Response.ok(user);
    }

    @DeleteMapping("/{id}")
    public Response<String> delete(@PathVariable int id) {
        userService.delete(id);
        return Response.ok();
    }

    @GetMapping("/{id}")
    public Response<User> get(@PathVariable int id) {
        User user = userService.detail(id);
        return Response.ok(user);
    }

    @GetMapping("/{name}/nearby-friends")
    public ListResponse<User> listNearbyFriends(@PathVariable String name,
                                                @RequestParam(name = "access_token", defaultValue = "") String accessToken,
                                                @RequestParam(defaultValue = "500.0") double radius,
                                                @RequestParam(defaultValue = "20") int limit) {
        oAuth2Util.checkPermission(accessToken);
        return userService.listNearbyFriends(name, radius, limit);
    }
}
