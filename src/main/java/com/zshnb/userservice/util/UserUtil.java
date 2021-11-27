package com.zshnb.userservice.util;

import static com.zshnb.userservice.common.UserConstant.KEY_USER_COORDINATE;

import com.zshnb.userservice.exception.InvalidArgumentException;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
	private final RedisTemplate<String, Integer> redisTemplate;

	public UserUtil(
		RedisTemplate<String, Integer> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void setAddress(String address, int id) {
		String[] strings = address.split(",");
		AssertionUtil.assertCondition(strings.length == 2, "invalid coordinate");
		GeoOperations<String, Integer> geoOperations = redisTemplate.opsForGeo();
		try {
			geoOperations.add(KEY_USER_COORDINATE, new Point(Double.parseDouble(strings[0]), Double.parseDouble(strings[1])), id);
		} catch (Exception e) {
			throw new InvalidArgumentException(String.format("invalid coordinate with [%s,%s]", strings[0], strings[1]), e);
		}
	}
}
