package com.zshnb.userservice.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.entity.Follow;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.mapper.FollowMapper;
import com.zshnb.userservice.mapper.UserMapper;
import com.zshnb.userservice.request.ListFollowUserRequest;
import com.zshnb.userservice.service.IFollowService;
import com.zshnb.userservice.util.AssertionUtil;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {
    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    public FollowServiceImpl(FollowMapper followMapper,
                             UserMapper userMapper) {
        this.followMapper = followMapper;
        this.userMapper = userMapper;
    }

    @Override
    public ListResponse<User> listFollowUser(ListFollowUserRequest request) {
        User user = userMapper.selectById(request.getUserId());
        AssertionUtil.assertCondition(user != null, String.format("user with %d doesn't exist", request.getUserId()));
        IPage<User> followUsers = followMapper.findFollowUserByUserId(
            new Page<Follow>(request.getPageNumber(), request.getPageSize()), request.getUserId());
        return new ListResponse<>(followUsers.getRecords(), followUsers.getTotal());
    }
}
