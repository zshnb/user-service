package com.zshnb.userservice.user_service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.zshnb.userservice.BaseTest;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class AddUserTest extends BaseTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void successful() {
        User user = new User();
        user.setName("first user");
        user.setDob(LocalDateTime.now());
        user.setAddress("address");
        user.setDescription("description");
        ResponseEntity<Response<User>> responseEntity = testRestTemplate.exchange("/api/user", HttpMethod.POST,
            new HttpEntity<>(user), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getData().getName()).isEqualTo("first user");
    }

    @Test
    public void failedWhenDuplicated() {
        User user = new User();
        user.setName("first user");
        user.setDob(LocalDateTime.now());
        user.setAddress("address");
        user.setDescription("description");
        ResponseEntity<Response<User>> responseEntity = testRestTemplate.exchange("/api/user", HttpMethod.POST,
            new HttpEntity<>(user), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getData().getName()).isEqualTo("first user");

        responseEntity = testRestTemplate.exchange("/api/user", HttpMethod.POST,
            new HttpEntity<>(user), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
