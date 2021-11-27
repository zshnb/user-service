package com.zshnb.userservice.serviceImpl;

import static com.zshnb.userservice.common.UserConstant.KEY_USER_COORDINATE;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.mapper.UserMapper;
import com.zshnb.userservice.request.AddUserRequest;
import com.zshnb.userservice.request.UpdateUserRequest;
import com.zshnb.userservice.service.IUserService;
import com.zshnb.userservice.util.AssertionUtil;
import com.zshnb.userservice.util.UserUtil;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.data.geo.Distance;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private final RedisTemplate<String, Integer> redisTemplate;
    private final UserMapper userMapper;
    private final UserUtil userUtil;

    public UserServiceImpl(RedisTemplate<String, Integer> redisTemplate,
                           UserMapper userMapper, UserUtil userUtil) {
        this.redisTemplate = redisTemplate;
        this.userMapper = userMapper;
        this.userUtil = userUtil;
    }

    @Override
    @Transactional
    public User add(AddUserRequest request) {
        AssertionUtil.assertCondition(!StringUtils.isEmpty(request.getName()), "name must not be empty");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.exists(String.format("select id from user where name = '%s'", request.getName()));
        AssertionUtil.assertCondition(getOne(queryWrapper) == null, String.format("user with name:[%s] already exist", request.getName()));
        User user = new User();
        BeanUtils.copyProperties(request, user);
        save(user);
        userUtil.setAddress(request.getAddress(), user.getId());
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
            userUtil.setAddress(request.getAddress(), id);
        }
        User user = getById(id);
        AssertionUtil.assertCondition(user != null, String.format("user with %d doesn't exist", id));
        if (StringUtils.isEmpty(request.getName())) {
            request.setName(user.getName());
        }
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
        ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.remove(KEY_USER_COORDINATE, id);
    }

    @Override
    public ListResponse<User> listNearbyFriends(String name, double radius, int limit) {
        User user = getOne(new QueryWrapper<User>().eq("name", name));
        AssertionUtil.assertCondition(user != null, String.format("user with name:[%s] doesn't exist", name));
        GeoOperations<String, Integer> geoOperations = redisTemplate.opsForGeo();
        GeoRadiusCommandArgs args = GeoRadiusCommandArgs.newGeoRadiusArgs()
            .limit(limit)
            .sortAscending();
        List<Integer> nearbyUserIds = geoOperations.radius(KEY_USER_COORDINATE, user.getId(), new Distance(radius), args)
            .getContent()
            .stream()
            .filter(it -> !it.getContent().getName().equals(user.getId()))
            .map(it -> it.getContent().getName())
            .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(nearbyUserIds)) {
            List<User> nearbyUsers = userMapper.findNearbyFriends(nearbyUserIds, user.getId());
            return new ListResponse<>(nearbyUsers, nearbyUsers.size());
        }
        return new ListResponse<>();
    }
}
