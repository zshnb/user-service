package com.zshnb.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zshnb.userservice.entity.Fan;
import com.zshnb.userservice.entity.Follow;
import com.zshnb.userservice.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FanMapper extends BaseMapper<Fan> {
    IPage<User> findFanUserByUserId(Page<?> page, @Param("userId") int userId);
}
