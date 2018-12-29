package com.korres.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import com.korres.dao.PluginConfigDao;
import com.korres.entity.PluginConfig;
import org.springframework.stereotype.Repository;

@Repository("pluginConfigDaoImpl")
public class PluginConfigDaoImpl extends BaseDaoImpl<PluginConfig, Long>
		implements PluginConfigDao {
	public boolean pluginIdExists(String pluginId) {
		if (pluginId == null)
			return false;
		String str = "select count(*) from PluginConfig pluginConfig where pluginConfig.pluginId = :pluginId";
		Long localLong = (Long) this.entityManager.createQuery(str, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("pluginId",
						pluginId).getSingleResult();
		return localLong.longValue() > 0L;
	}

	public PluginConfig findByPluginId(String pluginId) {
		if (pluginId == null)
			return null;
		try {
			String str = "select pluginConfig from PluginConfig pluginConfig where pluginConfig.pluginId = :pluginId";
			return (PluginConfig) this.entityManager.createQuery(str,
					PluginConfig.class).setFlushMode(FlushModeType.COMMIT)
					.setParameter("pluginId", pluginId).getSingleResult();
		} catch (NoResultException localNoResultException1) {
		}
		return null;
	}
}