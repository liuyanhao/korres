package com.korres;

import org.apache.commons.beanutils.converters.AbstractConverter;

/*
 * 类名：EnumConverter.java
 * 功能说明：enum反射类
 * 创建日期：2018-11-09 下午01:57:56
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class EnumConverter extends AbstractConverter {
	private final Class<?> enumClass;

	public EnumConverter(Class<?> enumClass) {
		this(enumClass, null);
	}

	public EnumConverter(Class<?> enumClass, Object defaultValue) {
		super(defaultValue);
		this.enumClass = enumClass;
	}

	protected Class<?> getDefaultType() {
		return this.enumClass;
	}

	protected Object convertToType(Class type, Object value) {
		String str = value.toString().trim();
		return Enum.valueOf(type, str);
	}

	protected String convertToString(Object value) {
		return value.toString();
	}
}