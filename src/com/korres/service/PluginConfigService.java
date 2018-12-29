package com.korres.service;

import com.korres.entity.PluginConfig;

public abstract interface PluginConfigService extends
		BaseService<PluginConfig, Long> {
	public abstract boolean pluginIdExists(String paramString);

	public abstract PluginConfig findByPluginId(String paramString);
}