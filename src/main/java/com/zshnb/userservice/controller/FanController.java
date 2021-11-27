package com.zshnb.userservice.controller;

import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.request.ListFanUserRequest;
import com.zshnb.userservice.serviceImpl.FanServiceImpl;
import com.zshnb.userservice.util.OAuth2Util;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FanController {
    private final FanServiceImpl fanService;
    private final OAuth2Util oAuth2Util;

    public FanController(FanServiceImpl fanService, OAuth2Util oAuth2Util) {
        this.fanService = fanService;
        this.oAuth2Util = oAuth2Util;
    }

    @GetMapping("/fan/{userId}/fan-users")
    public ListResponse<User> listFollowUser(@RequestParam(defaultValue = "1") int pageNumber,
                                             @RequestParam(defaultValue = "20") int pageSize,
                                             @RequestParam(defaultValue = "") String code,
                                             @RequestParam(name = "client_id", defaultValue = "") String clientId,
                                             @RequestParam(name = "secret", defaultValue = "") String clientSecret,
                                             @PathVariable int userId) {
        oAuth2Util.checkPermission(code, clientId, clientSecret);

        ListFanUserRequest request = new ListFanUserRequest();
        request.setUserId(userId);
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        return fanService.listFanUser(request);
    }
}
