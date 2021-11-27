package com.zshnb.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zshnb.userservice.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {

	List<User> findNearbyFriends(@Param("userIds") List<Integer> userIds, @Param("userId") int userId);
}
