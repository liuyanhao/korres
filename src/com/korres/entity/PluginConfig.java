package com.korres.entity;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.Transient;

/*
 * 类名：PluginConfig.java
 * 功能说明：插件配置实体类
 * 创建日期：2018-08-28 下午03:18:16
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_plugin_config")
public class PluginConfig extends OrderEntity {
	private static final long serialVersionUID = -4357367409438384806L;
	private String pluginId;
	private Boolean isEnabled;
	private Map<String, String> attributes = new HashMap<String, String>();

	@Column(nullable = false, updatable = false, unique = true)
	public String getPluginId() {
		return this.pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	@Column(nullable = false)
	public Boolean getIsEnabled() {
		return this.isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "xx_plugin_config_attribute")
	public Map<String, String> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	@Transient
	public String getAttribute(String name) {
		if ((getAttributes() != null) && (name != null))
			return (String) getAttributes().get(name);
		return null;
	}

	@Transient
	public void setAttribute(String name, String value) {
		if ((getAttributes() != null) && (name != null)) {
			getAttributes().put(name, value);
		}
	}
}