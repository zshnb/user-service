package com.zshnb.userservice.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Handle;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.zshnb.userservice.common.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class OAuth2Controller {

	@GetMapping("/oauth2/*")
	public Object handleRequest() {
		System.out.println("------- 进入请求: " + SaHolder.getRequest().getUrl());
		return SaOAuth2Handle.serverRequest();
	}

	@GetMapping("/oauth2/redirect-page")
	public Response<String> redirectPage(@RequestParam String code) {
		return Response.ok(code);
	}

	@GetMapping("/api/oauth2/user-info")
	public SaResult userInfo() {
		String accessToken = SaHolder.getRequest().getParamNotNull("access_token");
		SaOAuth2Util.checkScope(accessToken, "userinfo");
		return SaResult.data(null);
	}

	@Autowired
	public void setSaOAuth2Config(SaOAuth2Config cfg) {
		cfg.setNotLoginView(() -> new ModelAndView("login.html"))
			.setDoLoginHandle((name, pwd) -> {
				if (name.equals("wiredcraft") && pwd.equals("password")) {
					StpUtil.login(name);
					return SaResult.ok();
				}
				return SaResult.error("app not exist");
			}).setConfirmView((clientId, scope) -> {
				Map<String, Object> map = new HashMap<>();
				map.put("clientId", clientId);
				return new ModelAndView("confirm.html", map);
			});
	}
}
