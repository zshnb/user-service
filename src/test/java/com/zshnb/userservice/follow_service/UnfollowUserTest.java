package com.zshnb.userservice.follow_service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.zshnb.userservice.BaseTest;
import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.mapper.UserMapper;
import com.zshnb.userservice.request.FollowUserRequest;
import com.zshnb.userservice.request.UnfollowUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UnfollowUserTest extends BaseTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void unfollowSuccessful() {
        User user1 = new User();
        user1.setName("user1");
        User user2 = new User();
        user2.setName("user2");
        userMapper.insert(user1);
        userMapper.insert(user2);

        FollowUserRequest followUserRequest = new FollowUserRequest();
        followUserRequest.setUserId(user1.getId());
        followUserRequest.setFollowUserId(user2.getId());

        testRestTemplate.exchange("/api/follow", HttpMethod.POST,
            new HttpEntity<>(followUserRequest), new ParameterizedTypeReference<Response<String>>() {});
        ResponseEntity<ListResponse<User>> listResponseResponseEntity =
            testRestTemplate.exchange(String.format("/api/follow/%d/follow-users", user1.getId()),
                HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<User>>() {});
        assertThat(listResponseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponseResponseEntity.getBody().getTotal()).isEqualTo(1L);
        assertThat(listResponseResponseEntity.getBody().getData().get(0).getId()).isEqualTo(user2.getId());

        UnfollowUserRequest unfollowUserRequest = new UnfollowUserRequest();
        unfollowUserRequest.setUserId(user1.getId());
        unfollowUserRequest.setFollowUserId(user2.getId());
        testRestTemplate.exchange("/api/unfollow", HttpMethod.DELETE,
            new HttpEntity<>(unfollowUserRequest), new ParameterizedTypeReference<Response<String>>() {});
        listResponseResponseEntity =
            testRestTemplate.exchange(String.format("/api/follow/%d/follow-users", user1.getId()),
                HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<User>>() {});
        assertThat(listResponseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponseResponseEntity.getBody().getTotal()).isZero();
    }

    @Test
    public void failedWhenRelationNotExist() {
        User user1 = new User();
        user1.setName("user1");
        User user2 = new User();
        user2.setName("user2");
        userMapper.insert(user1);
        userMapper.insert(user2);

        UnfollowUserRequest unfollowUserRequest = new UnfollowUserRequest();
        unfollowUserRequest.setUserId(user1.getId());
        unfollowUserRequest.setFollowUserId(user2.getId());
        ResponseEntity<Response<String>> responseEntity = testRestTemplate.exchange("/api/unfollow", HttpMethod.DELETE,
            new HttpEntity<>(unfollowUserRequest), new ParameterizedTypeReference<Response<String>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
