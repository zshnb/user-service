package com.zshnb.userservice.user_service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.zshnb.userservice.BaseTest;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.request.AddUserRequest;
import com.zshnb.userservice.serviceImpl.UserServiceImpl;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;


public class DeleteUserTest extends BaseTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserServiceImpl userService;

    @Test
    @Transactional
    public void successful() {
        AddUserRequest request = new AddUserRequest();
        request.setName("first user");
        request.setDob(LocalDateTime.now());
        request.setAddress("100.00,50.0");
        request.setDescription("description");
        ResponseEntity<Response<User>> responseEntity = testRestTemplate.exchange("/api/user", HttpMethod.POST,
            new HttpEntity<>(request), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getData().getName()).isEqualTo("first user");
        User response = responseEntity.getBody().getData();

        testRestTemplate.exchange(String.format("/api/user/%d", response.getId()), HttpMethod.DELETE,
            null, new ParameterizedTypeReference<Response<User>>() {});
        User user = userService.getById(response.getId());
        assertThat(user).isNull();
    }

    @Test
    public void failedWhenNotExist() {
        ResponseEntity<Response<User>> responseEntity = testRestTemplate.exchange("/api/user/1", HttpMethod.DELETE,
            null, new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
