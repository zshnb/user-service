package com.zshnb.userservice.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.mapper.UserMapper;
import com.zshnb.userservice.service.IUserService;
import com.zshnb.userservice.util.AssertionUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Override
    @Transactional
    public User add(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.exists(String.format("select id from user where name = '%s'", user.getName()));
        AssertionUtil.assertCondition(getOne(queryWrapper) == null, String.format("user with %s already exist", user.getName()));
        save(user);
        return getById(user.getId());
    }

    @Override
    public User update(int id, User updateRequest) {
        User user = getById(id);
        AssertionUtil.assertCondition(user != null, String.format("user with %d doesn't exist", id));
        BeanUtils.copyProperties(updateRequest, user, "id", "createAt");
        updateById(user);
        return getById(user.getId());
    }

    @Override
    public User detail(int id) {
        User user = getById(id);
        AssertionUtil.assertCondition(user != null, String.format("user with %d doesn't exist", id));
        return user;
    }

    @Override
    public void delete(int id) {
        User user = getById(id);
        AssertionUtil.assertCondition(user != null, String.format("user with %d doesn't exist", id));
        getBaseMapper().deleteById(id);
    }
}
