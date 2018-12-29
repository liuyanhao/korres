package com.korres.interceptor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.korres.entity.Log;
import com.korres.service.AdminService;
import com.korres.service.LogConfigService;
import com.korres.service.LogService;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.korres.LogConfig;

public class LogInterceptor extends HandlerInterceptorAdapter {
	private static final String[] IGNORE_PARAMETERS = { "password",
			"rePassword", "currentPassword" };
	private static AntPathMatcher antPathMatcher = new AntPathMatcher();
	private String[] ignoreParameters = IGNORE_PARAMETERS;

	@Resource(name = "logConfigServiceImpl")
	private LogConfigService logConfigService;

	@Resource(name = "logServiceImpl")
	private LogService logService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		List<LogConfig> lilc = this.logConfigService.getAll();
		if (lilc != null) {
			String path = request.getServletPath();
			Iterator<LogConfig> iterator1 = lilc.iterator();
			while (iterator1.hasNext()) {
				LogConfig logConfig = iterator1.next();
				if (antPathMatcher.match(logConfig.getUrlPattern(), path)) {
					String operator = this.adminService.getCurrentUsername();
					String operation = logConfig.getOperation();
					String content = (String) request
							.getAttribute(Log.LOG_CONTENT_ATTRIBUTE_NAME);
					String ip = request.getRemoteAddr();
					request.removeAttribute(Log.LOG_CONTENT_ATTRIBUTE_NAME);
					StringBuffer stringBuffer = new StringBuffer();
					Map map = request.getParameterMap();
					if (map != null) {
						Iterator iterator2 = map.entrySet().iterator();
						while (iterator2.hasNext()) {
							Map.Entry<String, String[]> entry = (Entry<String, String[]>) iterator2
									.next();
							String ig = entry.getKey();
							if (!ArrayUtils.contains(this.ignoreParameters, ig)) {
								String[] arrayOfString = entry.getValue();
								if (arrayOfString != null)
									for (String str : arrayOfString) {
										stringBuffer.append(ig + " = " + str
												+ "\n");
									}
							}
						}
					}

					Log log = new Log();
					log.setOperation(operation);
					log.setOperator(operator);
					log.setContent(content);
					log.setParameter(stringBuffer.toString());
					log.setIp(ip);
					this.logService.save(log);
					break;
				}
			}
		}
	}

	public String[] getIgnoreParameters() {
		return this.ignoreParameters;
	}

	public void setIgnoreParameters(String[] ignoreParameters) {
		this.ignoreParameters = ignoreParameters;
	}
}