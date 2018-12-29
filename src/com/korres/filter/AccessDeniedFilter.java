package com.korres.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class AccessDeniedFilter implements Filter {
	private static final String ACCESS_DENIED = "Access denied!";

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain) {
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		try {
			response.sendError(403, ACCESS_DENIED);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}