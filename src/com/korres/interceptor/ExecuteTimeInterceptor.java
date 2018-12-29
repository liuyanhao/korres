package com.korres.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ExecuteTimeInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory
			.getLogger(ExecuteTimeInterceptor.class);
	private static final String START_TIME = ExecuteTimeInterceptor.class
			.getName()
			+ ".START_TIME";
	public static final String EXECUTE_TIME = ExecuteTimeInterceptor.class
			.getName()
			+ ".EXECUTE_TIME";
	private static final String REDIRECT = "redirect:";

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) {
		Long startTime = (Long) request.getAttribute(START_TIME);
		if (startTime == null) {
			startTime = Long.valueOf(System.currentTimeMillis());
			request.setAttribute(START_TIME, startTime);
		}
		return true;
	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		Long executeTime = (Long) request.getAttribute(EXECUTE_TIME);
		if (executeTime == null) {
			Long startTime = (Long) request.getAttribute(START_TIME);
			Long endTime = Long.valueOf(System.currentTimeMillis());
			executeTime = Long.valueOf(endTime.longValue()
					- startTime.longValue());
			request.setAttribute(START_TIME, startTime);
		}

		if (modelAndView != null) {
			String viewName = modelAndView.getViewName();
			if (!StringUtils.startsWith(viewName, REDIRECT))
				modelAndView.addObject(EXECUTE_TIME, executeTime);
		}

		if (logger.isDebugEnabled()) {
			logger
					.debug("[" + handler + "] executeTime: " + executeTime
							+ "ms");
		}
	}
}