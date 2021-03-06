package com.zshnb.userservice.user_service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zshnb.userservice.BaseTest;
import com.zshnb.userservice.common.ListResponse;
import com.zshnb.userservice.common.Response;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.exception.PermissionDenyException;
import com.zshnb.userservice.mapper.UserMapper;
import com.zshnb.userservice.request.AddUserRequest;
import com.zshnb.userservice.request.FollowUserRequest;
import com.zshnb.userservice.util.OAuth2Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ListNearbyFriendsTest extends BaseTest {
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@MockBean
	private OAuth2Util oAuth2Util;

	@BeforeEach
	public void prepareData() {
		// add users
		AddUserRequest request = new AddUserRequest();
		request.setName("user1");
		request.setAddress("100.00,50.0");
		User user1 = testRestTemplate.exchange("/api/user", HttpMethod.POST,
			new HttpEntity<>(request), new ParameterizedTypeReference<Response<User>>() {}).getBody().getData();
		request.setName("user2");
		request.setAddress("99.0,50.0");
		User user2 = testRestTemplate.exchange("/api/user", HttpMethod.POST,
			new HttpEntity<>(request), new ParameterizedTypeReference<Response<User>>() {}).getBody().getData();
		request.setName("user3");
		request.setAddress("55.6,67.4");
		User user3 = testRestTemplate.exchange("/api/user", HttpMethod.POST,
			new HttpEntity<>(request), new ParameterizedTypeReference<Response<User>>() {}).getBody().getData();
		request.setName("user4");
		request.setAddress("12.45,45.45");
		User user4 = testRestTemplate.exchange("/api/user", HttpMethod.POST,
			new HttpEntity<>(request), new ParameterizedTypeReference<Response<User>>() {}).getBody().getData();
		request.setName("user5");
		request.setAddress("10.45,45.0");
		User user5 = testRestTemplate.exchange("/api/user", HttpMethod.POST,
			new HttpEntity<>(request), new ParameterizedTypeReference<Response<User>>() {}).getBody().getData();

		// add follows
		FollowUserRequest followUserRequest = new FollowUserRequest();
		followUserRequest.setUserId(user1.getId());
		followUserRequest.setFollowUserId(user2.getId());
		testRestTemplate.exchange("/api/follow", HttpMethod.POST,
			new HttpEntity<>(followUserRequest), new ParameterizedTypeReference<Response<String>>() {});
		followUserRequest.setUserId(user1.getId());
		followUserRequest.setFollowUserId(user3.getId());
		testRestTemplate.exchange("/api/follow", HttpMethod.POST,
			new HttpEntity<>(followUserRequest), new ParameterizedTypeReference<Response<String>>() {});
		followUserRequest.setUserId(user2.getId());
		followUserRequest.setFollowUserId(user4.getId());
		testRestTemplate.exchange("/api/follow", HttpMethod.POST,
			new HttpEntity<>(followUserRequest), new ParameterizedTypeReference<Response<String>>() {});
		followUserRequest.setUserId(user5.getId());
		followUserRequest.setFollowUserId(user4.getId());
		testRestTemplate.exchange("/api/follow", HttpMethod.POST,
			new HttpEntity<>(followUserRequest), new ParameterizedTypeReference<Response<String>>() {});
	}

	@Test
	public void listSuccessful() {
		Mockito.doNothing().when(oAuth2Util).checkPermission(anyString());
		ResponseEntity<ListResponse<User>> listResponseResponseEntity =
			testRestTemplate.exchange("/api/user/user1/nearby-friends",
				HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<User>>() {});
		ListResponse<User> response = listResponseResponseEntity.getBody();
		assertThat(listResponseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getData().size()).isZero();

		listResponseResponseEntity = testRestTemplate.exchange("/api/user/user1/nearby-friends?radius=100000",
			HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<User>>() {});
		response = listResponseResponseEntity.getBody();
		assertThat(listResponseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getData().size()).isEqualTo(1);
		assertThat(response.getData().get(0).getName()).isEqualTo("user2");

		listResponseResponseEntity = testRestTemplate.exchange("/api/user/user4/nearby-friends?radius=200000",
			HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<User>>() {});
		response = listResponseResponseEntity.getBody();
		assertThat(listResponseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getData().size()).isEqualTo(1);
		assertThat(response.getData().get(0).getName()).isEqualTo("user5");

		User user = userMapper.selectOne(new QueryWrapper<User>().eq("name", "user5"));
		testRestTemplate.exchange(String.format("/api/user/%d", user.getId()), HttpMethod.DELETE,
			null, new ParameterizedTypeReference<Response<User>>() {});

		listResponseResponseEntity = testRestTemplate.exchange("/api/user/user4/nearby-friends?radius=200000",
			HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<User>>() {});
		response = listResponseResponseEntity.getBody();
		assertThat(listResponseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getData().size()).isZero();
	}

	@Test
	public void failedWhenPermissionDeny() {
		Mockito.doThrow(PermissionDenyException.class).when(oAuth2Util).checkPermission(anyString());
		ResponseEntity<ListResponse<User>> listResponseResponseEntity =
			testRestTemplate.exchange("/api/user/user1/nearby-friends",
				HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<User>>() {});
		assertThat(listResponseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}
}
