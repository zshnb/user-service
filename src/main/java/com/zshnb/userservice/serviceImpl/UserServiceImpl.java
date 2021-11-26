package com.zshnb.userservice.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.mapper.UserMapper;
import com.zshnb.userservice.request.AddUserRequest;
import com.zshnb.userservice.request.UpdateUserRequest;
import com.zshnb.userservice.service.IUserService;
import com.zshnb.userservice.util.AssertionUtil;
import com.zshnb.userservice.util.GeoUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private final RedisTemplate<String, Integer> redisTemplate;
    private final GeoUtil geoUtil;

    public UserServiceImpl(RedisTemplate<String, Integer> redisTemplate, GeoUtil geoUtil) {
        this.redisTemplate = redisTemplate;
        this.geoUtil = geoUtil;
    }

    @Override
    @Transactional
    public User add(AddUserRequest request) {
        AssertionUtil.assertCondition(!StringUtils.isEmpty(request.getName()), "name must not be empty");
        String[] strings = request.getAddress().split(",");
        AssertionUtil.assertCondition(strings.length == 2 && geoUtil.checkCoordinate(strings[0], strings[1]),
            "invalid coordinate");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.exists(String.format("select id from user where name = '%s'", request.getName()));
        AssertionUtil.assertCondition(getOne(queryWrapper) == null, String.format("user with name:[%s] already exist", request.getName()));
        User user = new User();
        BeanUtils.copyProperties(request, user);
        save(user);
        GeoOperations<String, Integer> geoOperations = redisTemplate.opsForGeo();
        geoOperations.add("user-coordinate", new Point(Double.parseDouble(strings[0]), Double.parseDouble(strings[1])), user.getId());
        return user;
    }

    @Override
    @Transactional
    public User update(int id, UpdateUserRequest request) {
        if (!StringUtils.isEmpty(request.getName())) {
            User maxExist = getOne(new QueryWrapper<User>().eq("name", request.getName()));
            AssertionUtil.assertCondition(maxExist == null, String.format("user with name:[%s] already exist", request.getName()));
        }
        if (!StringUtils.isEmpty(request.getAddress())) {
            String[] strings = request.getAddress().split(",");
            AssertionUtil.assertCondition(strings.length == 2 && geoUtil.checkCoordinate(strings[0], strings[1]),
                "invalid coordinate");
        }
        User user = getById(id);
        if (StringUtils.isEmpty(request.getName())) {
            request.setName(user.getName());
        }
        AssertionUtil.assertCondition(user != null, String.format("user with %d doesn't exist", id));
        BeanUtils.copyProperties(request, user);
        updateById(user);
        return user;
    }

    @Override
    public User detail(int id) {
        User user = getById(id);
        AssertionUtil.assertCondition(user != null, String.format("user with %d doesn't exist", id));
        return user;
    }

    @Override
    @Transactional
    public void delete(int id) {
        User user = getById(id);
        AssertionUtil.assertCondition(user != null, String.format("user with %d doesn't exist", id));
        getBaseMapper().deleteById(id);
    }
}
