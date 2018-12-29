package com.korres.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;

import com.korres.dao.AreaDao;
import com.korres.entity.Area;

import org.springframework.stereotype.Repository;

@Repository("areaDaoImpl")
public class AreaDaoImpl extends BaseDaoImpl<Area, Long> implements AreaDao {
	public List<Area> findRoots(Integer count) {
		String str = "select area from Area area where area.parent is null order by area.order asc";
		TypedQuery localTypedQuery = this.entityManager.createQuery(str,
				Area.class).setFlushMode(FlushModeType.COMMIT);
		if (count != null)
			localTypedQuery.setMaxResults(count.intValue());
		return localTypedQuery.getResultList();
	}
}