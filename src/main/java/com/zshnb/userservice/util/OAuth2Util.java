package com.zshnb.userservice.util;

import static com.zshnb.userservice.common.UserConstant.SERVER_URL;

import com.ejlchina.okhttps.OkHttps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zshnb.userservice.exception.PermissionDenyException;
import org.springframework.stereotype.Component;

@Component
public class OAuth2Util {
	public void checkPermission(String code, String clientId, String clientSecret) {
		String str = OkHttps.sync(SERVER_URL + "/oauth2/token")
			.addUrlPara("grant_type", "authorization_code")
			.addUrlPara("code", code)
			.addUrlPara("client_id", clientId)
			.addUrlPara("client_secret", clientSecret)
			.get()
			.getBody()
			.toString();
		JsonObject jsonObject = new Gson().fromJson(str, JsonObject.class);
		if(jsonObject == null || jsonObject.get("code").getAsInt() != 200) {
			throw new PermissionDenyException();
		}

		JsonObject data = jsonObject.get("data").getAsJsonObject();
		String accessToken = data.get("access_token").getAsString();
		str = OkHttps.sync(SERVER_URL + "/api/oauth2/user-info")
			.addUrlPara("access_token", accessToken)
			.get()
			.getBody()
			.toString();
		jsonObject = new Gson().fromJson(str, JsonObject.class);
		if(jsonObject.get("code").getAsInt() != 200) {
			throw new PermissionDenyException();
		}
	}
}
