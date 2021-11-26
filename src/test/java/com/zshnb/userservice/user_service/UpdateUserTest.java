package com.zshnb.userservice.user_service;

import com.zshnb.userservice.BaseTest;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.request.AddUserRequest;
import com.zshnb.userservice.request.UpdateUserRequest;
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
        AddUserRequest request = new AddUserRequest();
        request.setName("first user");
        request.setDob(LocalDateTime.now());
        request.setAddress("100.00,50.0");
        request.setDescription("description");
        ResponseEntity<Response<User>> responseEntity = testRestTemplate.exchange("/api/user", HttpMethod.POST,
            new HttpEntity<>(request), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        User response = responseEntity.getBody().getData();
        assertThat(response.getName()).isEqualTo("first user");

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("new name");
        responseEntity = testRestTemplate.exchange(String.format("/api/user/%d", response.getId()), HttpMethod.PUT,
            new HttpEntity<>(updateRequest), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getBody().getData().getName()).isEqualTo("new name");

        responseEntity = testRestTemplate.exchange(String.format("/api/user/%d", response.getId()), HttpMethod.PUT,
            new HttpEntity<>(new UpdateUserRequest()), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getBody().getData().getName()).isEqualTo("new name");
    }

    @Test
    public void failedWhenNotExist() {
        ResponseEntity<Response<User>> responseEntity = testRestTemplate.exchange("/api/user/1", HttpMethod.PUT,
            new HttpEntity<>(new User()), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void failedWhenInvalidAddress() {
        AddUserRequest request = new AddUserRequest();
        request.setName("first user");
        request.setDob(LocalDateTime.now());
        request.setAddress("100.0,50.0");
        request.setDescription("description");

        ResponseEntity<Response<User>> responseEntity = testRestTemplate.exchange("/api/user", HttpMethod.POST,
            new HttpEntity<>(request), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        User response = responseEntity.getBody().getData();
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setAddress("address");
        responseEntity = testRestTemplate.exchange(String.format("/api/user/%d", response.getId()), HttpMethod.PUT,
            new HttpEntity<>(updateRequest), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void failedWhenNameAlreadyExist() {
        AddUserRequest request = new AddUserRequest();
        request.setName("first user");
        request.setDob(LocalDateTime.now());
        request.setAddress("100.00,50.0");
        request.setDescription("description");
        ResponseEntity<Response<User>> responseEntity = testRestTemplate.exchange("/api/user", HttpMethod.POST,
            new HttpEntity<>(request), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        User response = responseEntity.getBody().getData();
        assertThat(response.getName()).isEqualTo("first user");

        request.setName("second user");
        testRestTemplate.exchange("/api/user", HttpMethod.POST,
            new HttpEntity<>(request), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("second user");
        responseEntity = testRestTemplate.exchange(String.format("/api/user/%d", response.getId()), HttpMethod.PUT,
            new HttpEntity<>(updateRequest), new ParameterizedTypeReference<Response<User>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
