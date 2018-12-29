package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.PluginConfigDao;
import com.korres.entity.PluginConfig;
import com.korres.service.PluginConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("pluginConfigServiceImpl")
public class PluginConfigServiceImpl extends
		BaseServiceImpl<PluginConfig, Long> implements PluginConfigService {

	@Resource(name = "pluginConfigDaoImpl")
	private PluginConfigDao pluginConfigDao;

	@Resource(name = "pluginConfigDaoImpl")
	public void setBaseDao(PluginConfigDao pluginConfigDao) {
		super.setBaseDao(pluginConfigDao);
	}

	@Transactional(readOnly = true)
	public boolean pluginIdExists(String pluginId) {
		return this.pluginConfigDao.pluginIdExists(pluginId);
	}

	@Transactional(readOnly = true)
	public PluginConfig findByPluginId(String pluginId) {
		return this.pluginConfigDao.findByPluginId(pluginId);
	}
}