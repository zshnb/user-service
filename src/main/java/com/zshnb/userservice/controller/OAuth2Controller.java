package com.zshnb.userservice.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Handle;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.serviceImpl.UserServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class OAuth2Controller {
	private final UserServiceImpl userService;

	public OAuth2Controller(UserServiceImpl userService) {
		this.userService = userService;
	}

	@GetMapping("/oauth2/*")
	public Object request() {
		return SaOAuth2Handle.serverRequest();
	}

	@Autowired
	public void setSaOAuth2Config(SaOAuth2Config cfg) {
		cfg.setNotLoginView(() -> new ModelAndView("login.html"))
			.setDoLoginHandle((name, pdw) -> {
				int id = Integer.parseInt(SaHolder.getRequest().getParam("id"));
				User user = userService.getById(id);
				if (user != null) {
					StpUtil.login(id);
					return SaResult.ok();
				}
				return SaResult.error("user not exist");
			}).setConfirmView((clientId, scope) -> {
				Map<String, Object> map = new HashMap<>();
				map.put("clientId", clientId);
				return new ModelAndView("confirm.html", map);
			});
	}
}
