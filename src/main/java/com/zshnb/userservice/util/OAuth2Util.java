package com.zshnb.userservice.util;

import static com.zshnb.userservice.common.UserConstant.SERVER_URL;

import com.ejlchina.okhttps.OkHttps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zshnb.userservice.exception.PermissionDenyException;
import org.springframework.stereotype.Component;

@Component
public class OAuth2Util {
	public void checkPermission(String accessToken) {
		String str = OkHttps.sync(SERVER_URL + "/api/oauth2/user-info")
			.addUrlPara("access_token", accessToken)
			.get()
			.getBody()
			.toString();
		JsonObject jsonObject = new Gson().fromJson(str, JsonObject.class);
		if(jsonObject.get("code").getAsInt() != 200) {
			throw new PermissionDenyException();
		}
	}
}
