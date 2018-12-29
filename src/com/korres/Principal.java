package com.korres;

import java.io.Serializable;

/*
 * 类名：Principal.java
 * 功能说明：用户登录安全对象
 * 创建日期：2013-8-14 下午04:02:12
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class Principal implements Serializable {
	private Long id;
	private String username;

	public Principal(Long id, String username) {
		this.id = id;
		this.username = username;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String toString() {
		return this.username;
	}
}