package com.korres.dao;

import com.korres.entity.PluginConfig;

public abstract interface PluginConfigDao extends BaseDao<PluginConfig, Long> {
	public abstract boolean pluginIdExists(String paramString);

	public abstract PluginConfig findByPluginId(String paramString);
}