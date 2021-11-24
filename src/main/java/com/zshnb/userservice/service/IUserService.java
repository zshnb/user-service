package com.zshnb.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zshnb.userservice.entity.User;

public interface IUserService extends IService<User> {
    User add(User user);
    User update(User user);
    User detail(int id);
    void delete(int id);
}
