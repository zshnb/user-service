package com.zshnb.userservice.util;

import com.ejlchina.okhttps.OkHttps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zshnb.userservice.config.OAuth2Config;
import com.zshnb.userservice.exception.PermissionDenyException;
import org.springframework.stereotype.Component;

@Component
public class OAuth2Util {
	private final OAuth2Config oAuth2Config;

	public OAuth2Util(OAuth2Config oAuth2Config) {
		this.oAuth2Config = oAuth2Config;
	}

	/**
	 * check if accessToken exist or has permission
	 * */
	public void checkPermission(String accessToken) {
		String str = OkHttps.sync(oAuth2Config.getServerUrl() + "/api/oauth2/user-info")
			.addUrlPara("access_token", accessToken)
			.get()
			.getBody()
			.toString();
		JsonObject jsonObject = new Gson().fromJson(str, JsonObject.class);
		if(jsonObject == null || jsonObject.get("code") == null || jsonObject.get("code").getAsInt() != 200) {
			throw new PermissionDenyException();
		}
	}
}
