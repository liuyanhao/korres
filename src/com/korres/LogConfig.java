package com.korres;

import java.io.Serializable;

/*
 * 类名：LogConfig.java
 * 功能说明：日志类
 * 创建日期：2013-8-9 下午01:55:11
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class LogConfig implements Serializable {
	private String operation;
	private String urlPattern;

	public String getOperation() {
		return this.operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getUrlPattern() {
		return this.urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
}