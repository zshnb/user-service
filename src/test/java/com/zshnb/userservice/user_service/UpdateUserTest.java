package com.zshnb.userservice.user_service;

import com.zshnb.userservice.BaseTest;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class UpdateUserTest extends BaseTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @Transactional
    public void successful() {
        User user = new User();
        user.setName("first user");
        user.setDob(LocalDateTime.now());
        user.setAddress("address");
        user.setDescription("description");
        ResponseEntity<Response<User>> responseEntity = testRestTemplate.exchange("/api/user", HttpMethod.POST,
            new HttpEntity<>(user), new ParameterizedTypeReference<Response<User>>() {});
        User response = responseEntity.getBody().getData();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getName()).isEqualTo("first user");

        User updateRequest = new User();
        updateRequest.setName("new name");
        responseEntity = testRestTemplate.exchange(String.format("/api/user/%d", response.getId()), HttpMethod.PUT,
            new HttpEntity<>(updateRequest), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getBody().getData().getName()).isEqualTo("new name");
    }

    @Test
    public void failedWhenNotExist() {
        ResponseEntity<Response<User>> responseEntity = testRestTemplate.exchange("/api/user/1", HttpMethod.PUT,
            new HttpEntity<>(new User()), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
