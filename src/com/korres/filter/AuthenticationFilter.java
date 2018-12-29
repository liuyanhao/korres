package com.korres.filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.korres.service.RSAService;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

public class AuthenticationFilter extends FormAuthenticationFilter {
	private static final String EN_PASSWORD = "enPassword";
	private static final String CAPTCHA_ID = "captchaId";
	private static final String CAPTCHA = "captcha";
	private String enPasswordParam = EN_PASSWORD;
	private String captchaIdParam = CAPTCHA_ID;
	private String captchaParam = CAPTCHA;

	@Resource(name = "rsaServiceImpl")
	private RSAService rsaService;

	protected org.apache.shiro.authc.AuthenticationToken createToken(
			ServletRequest servletRequest, ServletResponse servletResponse) {
		String username = getUsername(servletRequest);
		String password = getPassword(servletRequest);
		String captchaId = getSession(servletRequest);
		String captcha = getCleanParam(servletRequest);
		boolean bool = isRememberMe(servletRequest);
		String host = getHost(servletRequest);
		return new com.korres.AuthenticationToken(username, password,
				captchaId, captcha, bool, host);
	}

	protected boolean onAccessDenied(ServletRequest servletRequest,
			ServletResponse servletResponse) throws Exception {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String str = request.getHeader("X-Requested-With");
		if ((str != null) && (str.equalsIgnoreCase("XMLHttpRequest"))) {
			response.addHeader("loginStatus", "accessDenied");
			response.sendError(403);
			return false;
		}
		return super.onAccessDenied(request, response);
	}

	protected boolean onLoginSuccess(
			org.apache.shiro.authc.AuthenticationToken token, Subject subject,
			ServletRequest servletRequest, ServletResponse servletResponse)
			throws Exception {
		Session session = subject.getSession();
		Map<Object, Object> map = new HashMap<Object, Object>();
		Collection<Object> collection = session.getAttributeKeys();
		Iterator<Object> iterator = collection.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			map.put(obj, session.getAttribute(obj));
		}

		session.stop();
		session = subject.getSession();
		Iterator<Map.Entry<Object,Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Object,Object> entry = it.next();
			session.setAttribute(entry.getKey(), entry.getValue());
		}

		return super.onLoginSuccess(token, subject, servletRequest,
				servletResponse);
	}

	protected String getPassword(ServletRequest servletRequest) {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String password = this.rsaService.decryptParameter(this.enPasswordParam,
				request);
		this.rsaService.removePrivateKey(request);
		
		return password;
	}

	protected String getSession(ServletRequest paramServletRequest) {
		String str = WebUtils.getCleanParam(paramServletRequest,
				this.captchaIdParam);
		if (str == null){
			str = ((HttpServletRequest) paramServletRequest).getSession()
					.getId();
		}
		
		return str;
	}

	protected String getCleanParam(ServletRequest paramServletRequest) {
		return WebUtils.getCleanParam(paramServletRequest, this.captchaParam);
	}

	public String getEnPasswordParam() {
		return this.enPasswordParam;
	}

	public void setEnPasswordParam(String enPasswordParam) {
		this.enPasswordParam = enPasswordParam;
	}

	public String getCaptchaIdParam() {
		return this.captchaIdParam;
	}

	public void setCaptchaIdParam(String captchaIdParam) {
		this.captchaIdParam = captchaIdParam;
	}

	public String getCaptchaParam() {
		return this.captchaParam;
	}

	public void setCaptchaParam(String captchaParam) {
		this.captchaParam = captchaParam;
	}
}