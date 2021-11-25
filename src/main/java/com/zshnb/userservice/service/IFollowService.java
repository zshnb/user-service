package com.zshnb.userservice.service;

import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.request.ListFollowUserRequest;

public interface IFollowService {

    ListResponse<User> listFollowUser(ListFollowUserRequest request);
}
