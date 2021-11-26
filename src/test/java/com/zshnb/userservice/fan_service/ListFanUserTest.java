package com.zshnb.userservice.fan_service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.zshnb.userservice.BaseTest;
import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.mapper.UserMapper;
import com.zshnb.userservice.request.FollowUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ListFanUserTest extends BaseTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void listFanUserSuccessful() {
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

        ResponseEntity<ListResponse<User>> listResponseResponseEntity =
            testRestTemplate.exchange(String.format("/api/fan/%d/fan-users", user2.getId()),
                HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<User>>() {});
        assertThat(listResponseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponseResponseEntity.getBody().getTotal()).isEqualTo(1L);
        assertThat(listResponseResponseEntity.getBody().getData().get(0).getId()).isEqualTo(user1.getId());
    }
}
