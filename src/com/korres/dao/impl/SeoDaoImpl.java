package com.korres.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import com.korres.dao.SeoDao;
import com.korres.entity.Seo;
import com.korres.entity.Seo.SeoType;

import org.springframework.stereotype.Repository;

@Repository("seoDaoImpl")
public class SeoDaoImpl extends BaseDaoImpl<Seo, Long> implements SeoDao {
	public Seo find(SeoType type) {
		if (type == null)
			return null;
		try {
			String str = "select seo from Seo seo where seo.type = :type";
			return (Seo) this.entityManager.createQuery(str, Seo.class)
					.setFlushMode(FlushModeType.COMMIT).setParameter("type",
							type).getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		return null;
	}
}