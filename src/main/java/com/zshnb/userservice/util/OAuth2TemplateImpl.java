package com.zshnb.userservice.util;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import com.zshnb.userservice.entity.User;
import com.zshnb.userservice.serviceImpl.UserServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class OAuth2TemplateImpl extends SaOAuth2Template {
	private final UserServiceImpl userService;

	public OAuth2TemplateImpl(UserServiceImpl userService) {
		this.userService = userService;
	}

	@Override
	public SaClientModel getClientModel(String clientId) {
		User user = userService.getById(Integer.parseInt(clientId));
		if(user != null) {
			return new SaClientModel()
				.setClientId(user.getId().toString())
				.setClientSecret("secret")
				.setAllowUrl("*")
				.setContractScope("userinfo");
		}
		return null;
	}
}
