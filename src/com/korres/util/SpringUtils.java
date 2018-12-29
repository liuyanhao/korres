package com.korres.util;

import java.util.Locale;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.LocaleResolver;

/*
 * 类名：SpringUtils.java
 * 功能说明：spring 上下文工具类
 * 创建日期：2018-12-14 下午04:08:10
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Component("springUtils")
@Lazy(false)
public final class SpringUtils implements DisposableBean,
		ApplicationContextAware {
	private static ApplicationContext ctx;

	public void setApplicationContext(ApplicationContext applicationContext) {
		ctx = applicationContext;
	}

	public void destroy() {
		ctx = null;
	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	public static Object getBean(String name) {
		Assert.hasText(name);
		return ctx.getBean(name);
	}

	public static <T> T getBean(String name, Class<T> type) {
		Assert.hasText(name);
		Assert.notNull(type);
		return ctx.getBean(name, type);
	}

	public static String getMessage(String code, Object[] args) {
		LocaleResolver localLocaleResolver = (LocaleResolver) getBean(
				"localeResolver", LocaleResolver.class);
		Locale localLocale = localLocaleResolver.resolveLocale(null);
		return ctx.getMessage(code, args, localLocale);
	}
}