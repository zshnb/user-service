package com.zshnb.userservice.controller;

import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.request.AddUserRequest;
import com.zshnb.userservice.request.UpdateUserRequest;
import com.zshnb.userservice.serviceImpl.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/user")
public class UserController {
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseBody
    public Response<User> add(@RequestBody AddUserRequest request) {
        User user = userService.add(request);
        return Response.ok(user);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public Response<User> update(@PathVariable int id, @RequestBody UpdateUserRequest request) {
        User user = userService.update(id, request);
        return Response.ok(user);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Response<String> delete(@PathVariable int id) {
        userService.delete(id);
        return Response.ok();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Response<User> get(@PathVariable int id) {
        User user = userService.detail(id);
        return Response.ok(user);
    }
}
