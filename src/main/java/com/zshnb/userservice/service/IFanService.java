package com.zshnb.userservice.service;

import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.request.ListFanUserRequest;

public interface IFanService {
    ListResponse<User> listFanUser(ListFanUserRequest request);
}
