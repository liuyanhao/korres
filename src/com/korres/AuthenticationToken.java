package com.korres;

import org.apache.shiro.authc.UsernamePasswordToken;

/*
 * 类名：AuthenticationToken.java
 * 功能说明：登录token令牌
 * 创建日期：2018-12-14 下午04:04:27
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class AuthenticationToken extends UsernamePasswordToken {
	private String captchaId;
	private String captcha;

	public AuthenticationToken(String username, String password,
			String captchaId, String captcha, boolean rememberMe, String host) {
		super(username, password, rememberMe);
		this.captchaId = captchaId;
		this.captcha = captcha;
	}

	public String getCaptchaId() {
		return this.captchaId;
	}

	public void setCaptchaId(String captchaId) {
		this.captchaId = captchaId;
	}

	public String getCaptcha() {
		return this.captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
}