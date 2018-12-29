package com.korres.interceptor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.korres.util.CookieUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class TokenInterceptor extends HandlerInterceptorAdapter {
	private static final String IIIllIlI = "token";
	private static final String IIIllIll = "token";
	private static final String IIIlllII = "token";
	private static final String BAD_MISSING_TOKEN = "Bad or missing token!";

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws UnsupportedEncodingException,IOException {
		String cookie = CookieUtils.getCookie(request, "token");
		if (request.getMethod().equalsIgnoreCase("POST")) {
			String str2 = request.getHeader("X-Requested-With");
			if ((str2 != null) && (str2.equalsIgnoreCase("XMLHttpRequest"))) {
				if ((cookie != null) && (cookie.equals(request.getHeader("token")))){
					return true;
				}
				
				response.addHeader("tokenStatus", "accessDenied");
			} else if ((cookie != null)
					&& (cookie.equals(request.getParameter("token")))) {
				return true;
			}
			if (cookie == null) {
				cookie = UUID.randomUUID().toString();
				CookieUtils.addCookie(request, response, "token", cookie);
			}
			response.sendError(403, BAD_MISSING_TOKEN);
			return false;
		}
		if (cookie == null) {
			cookie = UUID.randomUUID().toString();
			CookieUtils.addCookie(request, response, "token", cookie);
		}
		
		request.setAttribute("token", cookie);
		
		return true;
	}
}