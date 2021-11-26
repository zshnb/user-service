package com.zshnb.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.request.AddUserRequest;
import com.zshnb.userservice.request.UpdateUserRequest;

public interface IUserService extends IService<User> {
    User add(AddUserRequest request);
    User update(int id, UpdateUserRequest request);
    User detail(int id);
    void delete(int id);
}
