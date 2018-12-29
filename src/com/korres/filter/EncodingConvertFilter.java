package com.korres.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class EncodingConvertFilter extends OncePerRequestFilter {
	private String fromEncoding = "ISO-8859-1";
	private String toEncoding = "UTF-8";

	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (request.getMethod().equalsIgnoreCase("GET")) {
			Iterator iterator = request.getParameterMap().values().iterator();
			while (iterator.hasNext()) {
				String[] arrayOfString = (String[]) iterator.next();
				for (int i = 0; i < arrayOfString.length; i++)
					try {
						arrayOfString[i] = new String(
								arrayOfString[i].getBytes(this.fromEncoding),
								this.toEncoding);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
			}
		}
		
		filterChain.doFilter(request, response);
	}

	public String getFromEncoding() {
		return this.fromEncoding;
	}

	public void setFromEncoding(String fromEncoding) {
		this.fromEncoding = fromEncoding;
	}

	public String getToEncoding() {
		return this.toEncoding;
	}

	public void setToEncoding(String toEncoding) {
		this.toEncoding = toEncoding;
	}
}