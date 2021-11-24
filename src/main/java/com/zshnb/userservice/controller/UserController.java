package com.zshnb.userservice.controller;

import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
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
    public Response<User> add(@RequestBody User request) {
        User user = userService.add(request);
        return Response.ok(user);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public Response<User> update(@PathVariable int id, @RequestBody User old) {
        User user = userService.update(old);
        return Response.ok(user);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Response<String> delete(@PathVariable int id) {
        userService.delete(id);
        return Response.ok();
    }
}
