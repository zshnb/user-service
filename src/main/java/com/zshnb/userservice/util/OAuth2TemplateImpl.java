package com.zshnb.userservice.util;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import org.springframework.stereotype.Component;

@Component
public class OAuth2TemplateImpl extends SaOAuth2Template {
	@Override
	public SaClientModel getClientModel(String clientId) {
		if (!clientId.equals("wiredcraft")) {
			return null;
		}
		return new SaClientModel()
			.setClientId(clientId)
			.setClientSecret("secret")
			.setAllowUrl("*")
			.setContractScope("userinfo");
	}
}
