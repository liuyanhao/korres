package com.korres;

import java.io.Serializable;

/*
 * 类名：Template.java
 * 功能说明：korres.xml文件template属性的映射对象
 * 创建日期：2013-8-9 下午01:50:05
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class Template implements Serializable {
	private String id;
	private TemplateType type;
	private String name;
	private String templatePath;
	private String staticPath;
	private String description;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TemplateType getType() {
		return this.type;
	}

	public void setType(TemplateType type) {
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplatePath() {
		return this.templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public String getStaticPath() {
		return this.staticPath;
	}

	public void setStaticPath(String staticPath) {
		this.staticPath = staticPath;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public enum TemplateType {
		page, mail, print;
	}
}