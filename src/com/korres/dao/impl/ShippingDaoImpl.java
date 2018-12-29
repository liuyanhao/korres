package com.korres.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import com.korres.dao.ShippingDao;
import com.korres.entity.Shipping;

import org.springframework.stereotype.Repository;

@Repository("shippingDaoImpl")
public class ShippingDaoImpl extends BaseDaoImpl<Shipping, Long> implements
		ShippingDao {
	public Shipping findBySn(String sn) {
		if (sn == null)
			return null;
		String str = "select shipping from Shipping shipping where lower(shipping.sn) = lower(:sn)";
		try {
			return (Shipping) this.entityManager.createQuery(str,
					Shipping.class).setFlushMode(FlushModeType.COMMIT)
					.setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		return null;
	}
}