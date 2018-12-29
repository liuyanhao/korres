package com.korres.interceptor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.korres.entity.Member;
import com.korres.service.MemberService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.korres.Principal;

public class MemberInterceptor extends HandlerInterceptorAdapter {
	private static final String REDIRECT = "redirect:";
	private static final String REDIRECT_URL = "redirectUrl";
	private static final String ATTRIBUTE_NAME = "member";
	private static final String LOGIN_URL = "/login.jhtml";
	private String loginUrl = LOGIN_URL;

	@Value("${url_escaping_charset}")
	private String charset;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler)
			throws UnsupportedEncodingException, IOException {
		HttpSession session = request.getSession();
		Principal principal = (Principal) session
				.getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
		if (principal != null) {
			return true;
		}

		String header = request.getHeader("X-Requested-With");
		if ((header != null) && (header.equalsIgnoreCase("XMLHttpRequest"))) {
			response.addHeader("loginStatus", "accessDenied");
			try {
				response.sendError(403);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		if (request.getMethod().equalsIgnoreCase("GET")) {
			String url = request.getQueryString() != null ? request
					.getRequestURI()
					+ "?" + request.getQueryString() : request.getRequestURI();
			response.sendRedirect(request.getContextPath() + this.loginUrl
					+ "?" + REDIRECT_URL + "="
					+ URLEncoder.encode(url, this.charset));
		} else {
			response.sendRedirect(request.getContextPath() + this.loginUrl);
		}
		return false;
	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		if (modelAndView != null) {
			String viewName = modelAndView.getViewName();
			if (!StringUtils.startsWith(viewName, REDIRECT))
				modelAndView.addObject(ATTRIBUTE_NAME, this.memberService
						.getCurrent());
		}
	}

	public String getLoginUrl() {
		return this.loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
}