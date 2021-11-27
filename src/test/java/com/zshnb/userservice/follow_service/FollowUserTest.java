package com.zshnb.userservice.follow_service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.zshnb.userservice.BaseTest;
import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.Follow;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.mapper.UserMapper;
import com.zshnb.userservice.request.FollowUserRequest;
import com.zshnb.userservice.request.ListFollowUserRequest;
import com.zshnb.userservice.serviceImpl.FollowServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class FollowUserTest extends BaseTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FollowServiceImpl followService;

    @Test
    public void followSuccessful() {
        User user1 = new User();
        user1.setName("user1");
        User user2 = new User();
        user2.setName("user2");
        userMapper.insert(user1);
        userMapper.insert(user2);

        FollowUserRequest followUserRequest = new FollowUserRequest();
        followUserRequest.setUserId(user1.getId());
        followUserRequest.setFollowUserId(user2.getId());

        ResponseEntity<Response<String>> responseEntity = testRestTemplate.exchange("/api/follow", HttpMethod.POST,
            new HttpEntity<>(followUserRequest), new ParameterizedTypeReference<Response<String>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        ListFollowUserRequest listFollowUserRequest = new ListFollowUserRequest();
        listFollowUserRequest.setUserId(user1.getId());
        ListResponse<User> follows = followService.listFollowUser(listFollowUserRequest);
        assertThat(follows.getTotal()).isEqualTo(1L);
        assertThat(follows.getData().get(0).getId()).isEqualTo(user2.getId());
    }

    @Test
    public void failedWhenUserNotExist() {
        User user = new User();
        user.setName("user2");
        userMapper.insert(user);

        FollowUserRequest followUserRequest = new FollowUserRequest();
        followUserRequest.setUserId(0);
        followUserRequest.setFollowUserId(user.getId());

        ResponseEntity<Response<String>> responseEntity = testRestTemplate.exchange("/api/follow", HttpMethod.POST,
            new HttpEntity<>(followUserRequest), new ParameterizedTypeReference<Response<String>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void failedWhenFollowSelf() {
        User user = new User();
        user.setName("user2");
        userMapper.insert(user);

        FollowUserRequest followUserRequest = new FollowUserRequest();
        followUserRequest.setUserId(user.getId());
        followUserRequest.setFollowUserId(user.getId());

        ResponseEntity<Response<String>> responseEntity = testRestTemplate.exchange("/api/follow", HttpMethod.POST,
            new HttpEntity<>(followUserRequest), new ParameterizedTypeReference<Response<String>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void failedWhenFollowUserNotExist() {
        User user = new User();
        user.setName("user2");
        userMapper.insert(user);

        FollowUserRequest followUserRequest = new FollowUserRequest();
        followUserRequest.setUserId(user.getId());
        followUserRequest.setFollowUserId(0);

        ResponseEntity<Response<String>> responseEntity = testRestTemplate.exchange("/api/follow", HttpMethod.POST,
            new HttpEntity<>(followUserRequest), new ParameterizedTypeReference<Response<String>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
