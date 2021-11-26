package com.zshnb.userservice.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.Fan;
import com.zshnb.userservice.entity.Follow;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.mapper.FanMapper;
import com.zshnb.userservice.mapper.FollowMapper;
import com.zshnb.userservice.mapper.UserMapper;
import com.zshnb.userservice.request.FollowUserRequest;
import com.zshnb.userservice.request.ListFollowUserRequest;
import com.zshnb.userservice.request.UnfollowUserRequest;
import com.zshnb.userservice.service.IFollowService;
import com.zshnb.userservice.util.AssertionUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {
    private final FollowMapper followMapper;
    private final UserMapper userMapper;
    private final FanMapper fanMapper;

    public FollowServiceImpl(FollowMapper followMapper,
                             UserMapper userMapper,
                             FanMapper fanMapper) {
        this.followMapper = followMapper;
        this.userMapper = userMapper;
        this.fanMapper = fanMapper;
    }

    @Override
    public ListResponse<User> listFollowUser(ListFollowUserRequest request) {
        User user = userMapper.selectById(request.getUserId());
        AssertionUtil.assertCondition(user != null, String.format("user with %d doesn't exist", request.getUserId()));
        IPage<User> followUsers = followMapper.findFollowUserByUserId(
            new Page<Follow>(request.getPageNumber(), request.getPageSize()), request.getUserId());
        return new ListResponse<>(followUsers.getRecords(), followUsers.getTotal());
    }

    @Override
    @Transactional
    public Response<String> followUser(FollowUserRequest request) {
        User user = userMapper.selectById(request.getUserId());
        AssertionUtil.assertCondition(user != null, String.format("user with %d doesn't exist", request.getUserId()));
        User followUser = userMapper.selectById(request.getFollowUserId());
        AssertionUtil.assertCondition(followUser != null, String.format("follow user with %d doesn't exist", request.getFollowUserId()));
        Follow follow = new Follow();
        BeanUtils.copyProperties(request, follow);
        save(follow);
        Fan fan = new Fan();
        fan.setUserId(followUser.getId());
        fan.setFanUserId(user.getId());
        fanMapper.insert(fan);
        return Response.ok();
    }

    @Override
    public Response<String> unfollowUser(UnfollowUserRequest request) {
        User user = userMapper.selectById(request.getUserId());
        AssertionUtil.assertCondition(user != null, String.format("user with %d doesn't exist", request.getUserId()));
        User followUser = userMapper.selectById(request.getFollowUserId());
        AssertionUtil.assertCondition(followUser != null, String.format("follow user with %d doesn't exist", request.getFollowUserId()));
        QueryWrapper<Follow> queryWrapper = new QueryWrapper<Follow>()
            .eq("user_id", request.getUserId())
            .eq("follow_user_id", request.getFollowUserId());
        Follow follow = getOne(queryWrapper);
        AssertionUtil.assertCondition(follow != null,
            String.format("relationship between %d and %d doesn't exist", request.getUserId(), request.getFollowUserId()));
        followMapper.deleteById(follow.getId());
        fanMapper.delete(new QueryWrapper<Fan>().eq("user_id", followUser.getId())
            .eq("fan_user_id", user.getId()));
        return Response.ok();
    }
}
