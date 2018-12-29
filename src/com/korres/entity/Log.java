package com.korres.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/*
 * 类名：Log.java
 * 功能说明：日志实体类
 * 创建日期：2013-8-28 下午03:02:12
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_log")
public class Log extends BaseEntity {
	private static final long serialVersionUID = -4494144902110236826L;
	public static final String LOG_CONTENT_ATTRIBUTE_NAME = Log.class.getName()
			+ ".CONTENT";
	private String operation;
	private String operator;
	private String content;
	private String parameter;
	private String ip;

	@Column(nullable = false, updatable = false)
	public String getOperation() {
		return this.operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	@Column(updatable = false)
	public String getOperator() {
		return this.operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Column(length = 3000, updatable = false)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Lob
	@Column(updatable = false)
	public String getParameter() {
		return this.parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	@Column(nullable = false, updatable = false)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}