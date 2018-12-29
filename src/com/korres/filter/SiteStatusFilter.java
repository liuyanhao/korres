package com.korres.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.korres.util.SettingUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.korres.Setting;

@Component("siteStatusFilter")
public class SiteStatusFilter extends OncePerRequestFilter {
	private static final String[] IGNORE_URL_PATTERNS = { "/admin/**" };
	private static final String REDIRECT_URL = "/common/site_close.jhtml";
	private static AntPathMatcher antPathMatcher = new AntPathMatcher();
	private String[] ignoreUrlPatterns = IGNORE_URL_PATTERNS;
	private String redirectUrl = REDIRECT_URL;

	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Setting setting = SettingUtils.get();
		if (setting.getIsSiteEnabled().booleanValue()) {
			filterChain.doFilter(request, response);
		} else {
			String url = request.getServletPath();
			if (url.equals(this.redirectUrl)) {
				filterChain.doFilter(request, response);
				return;
			}
			if (this.ignoreUrlPatterns != null)
				for (String iup : this.ignoreUrlPatterns)
					if (antPathMatcher.match(iup, url)) {
						filterChain.doFilter(request, response);
						return;
					}
			
			response.sendRedirect(request.getContextPath() + this.redirectUrl);
		}
	}

	public String[] getIgnoreUrlPatterns() {
		return this.ignoreUrlPatterns;
	}

	public void setIgnoreUrlPatterns(String[] ignoreUrlPatterns) {
		this.ignoreUrlPatterns = ignoreUrlPatterns;
	}

	public String getRedirectUrl() {
		return this.redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}