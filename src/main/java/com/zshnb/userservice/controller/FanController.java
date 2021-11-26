package com.zshnb.userservice.controller;

import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.request.ListFanUserRequest;
import com.zshnb.userservice.serviceImpl.FanServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FanController {
    private final FanServiceImpl fanService;

    public FanController(FanServiceImpl fanService) {
        this.fanService = fanService;
    }

    @GetMapping("/fan/{userId}/fan-users")
    public ListResponse<User> listFollowUser(@RequestParam(defaultValue = "1") int pageNumber,
                                             @RequestParam(defaultValue = "20") int pageSize,
                                             @PathVariable int userId) {
        ListFanUserRequest request = new ListFanUserRequest();
        request.setUserId(userId);
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        return fanService.listFanUser(request);
    }
}
