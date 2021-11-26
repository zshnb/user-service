package com.zshnb.userservice.service;

import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.request.FollowUserRequest;
import com.zshnb.userservice.request.ListFollowUserRequest;
import com.zshnb.userservice.request.UnfollowUserRequest;

public interface IFollowService {

    ListResponse<User> listFollowUser(ListFollowUserRequest request);

    Response<String> followUser(FollowUserRequest request);

    Response<String> unfollowUser(UnfollowUserRequest request);
}
