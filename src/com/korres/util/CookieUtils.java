package com.korres.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.korres.Setting;

/*
 * 类名：CookieUtils.java
 * 功能说明：cookie工具类
 * 创建日期：2018-11-09 下午02:05:16
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public final class CookieUtils {
	
	private static final Logger logger = Logger.getLogger(CookieUtils.class);
	
	public static void addCookie(HttpServletRequest request,
			HttpServletResponse response, String name, String value,
			Integer maxAge, String path, String domain, Boolean secure) {
		Assert.notNull(request);
		Assert.notNull(response);
		Assert.hasText(name);
		try {
			name = URLEncoder.encode(name, "UTF-8");
			value = URLEncoder.encode(value, "UTF-8");
			Cookie cookie = new Cookie(name, value);
			if (maxAge != null) {
				cookie.setMaxAge(maxAge.intValue());
			}
			if (StringUtils.isNotEmpty(path)) {
				cookie.setPath(path);
			}
			if (StringUtils.isNotEmpty(domain)) {
				cookie.setDomain(domain);
			}
			if (secure != null) {
				cookie.setSecure(secure.booleanValue());
			}
			response.addCookie(cookie);
		} catch (UnsupportedEncodingException e) {
			logger.error("-------1---------addCookie error:\n" + e);
		}
	}

	public static void addCookie(HttpServletRequest request,
			HttpServletResponse response, String name, String value,
			Integer maxAge) {
		Setting setting = SettingUtils.get();
		addCookie(request, response, name, value, maxAge, setting
				.getCookiePath(), setting.getCookieDomain(), null);
	}

	public static void addCookie(HttpServletRequest request,
			HttpServletResponse response, String name, String value) {
		Setting setting = SettingUtils.get();
		addCookie(request, response, name, value, null,
				setting.getCookiePath(), setting.getCookieDomain(), null);
	}

	public static String getCookie(HttpServletRequest request, String name) {
		Assert.notNull(request);
		Assert.hasText(name);
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			try {
				name = URLEncoder.encode(name, "UTF-8");
				for (Cookie cookie : cookies)
					if (name.equals(cookie.getName()))
						return URLDecoder.decode(cookie.getValue(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("-------1---------getCookie error:\n" + e);
			}
		return null;
	}

	public static void removeCookie(HttpServletRequest request,
			HttpServletResponse response, String name, String path,
			String domain) {
		Assert.notNull(request);
		Assert.notNull(response);
		Assert.hasText(name);
		try {
			name = URLEncoder.encode(name, "UTF-8");
			Cookie cookie = new Cookie(name, null);
			cookie.setMaxAge(0);
			if (StringUtils.isNotEmpty(path))
				cookie.setPath(path);
			if (StringUtils.isNotEmpty(domain))
				cookie.setDomain(domain);
			response.addCookie(cookie);
		} catch (UnsupportedEncodingException e) {
			logger.error("-------1---------removeCookie error:\n" + e);
		}
	}

	public static void removeCookie(HttpServletRequest request,
			HttpServletResponse response, String name) {
		Setting setting = SettingUtils.get();
		removeCookie(request, response, name, setting.getCookiePath(), setting
				.getCookieDomain());
	}
}