package com.korres.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Query;
import com.korres.dao.LogDao;
import com.korres.entity.Log;
import org.springframework.stereotype.Repository;

@Repository("logDaoImpl")
public class LogDaoImpl extends BaseDaoImpl<Log, Long> implements LogDao {
	public void removeAll() {
		String str = "delete from Log log";
		this.entityManager.createQuery(str).setFlushMode(FlushModeType.COMMIT)
				.executeUpdate();
	}
}