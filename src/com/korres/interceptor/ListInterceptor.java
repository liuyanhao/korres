package com.korres.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.korres.util.CookieUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ListInterceptor extends HandlerInterceptorAdapter {
	private static final String REDIRECT = "redirect:";
	private static final String LIST_QUERY = "listQuery";

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		if ((modelAndView != null) && (modelAndView.isReference())) {
			String viewName = modelAndView.getViewName();
			if (StringUtils.startsWith(viewName, REDIRECT)) {
				String cookie = CookieUtils.getCookie(request, LIST_QUERY);
				if (StringUtils.isNotEmpty(cookie)) {
					if (StringUtils.startsWith(cookie, "?")) {
						cookie = cookie.substring(1);
					}

					if (StringUtils.contains(viewName, "?")) {
						modelAndView.setViewName(viewName + "&" + cookie);
					} else {
						modelAndView.setViewName(viewName + "?" + cookie);
					}
					CookieUtils.removeCookie(request, response, LIST_QUERY);
				}
			}
		}
	}
}