package com.korres.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import com.korres.dao.DeliveryCenterDao;
import com.korres.entity.DeliveryCenter;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository("deliveryCenterDaoImpl")
public class DeliveryCenterDaoImpl extends BaseDaoImpl<DeliveryCenter, Long>
		implements DeliveryCenterDao {
	public DeliveryCenter findDefault() {
		try {
			String str = "select deliveryCenter from DeliveryCenter deliveryCenter where deliveryCenter.isDefault = true";
			return (DeliveryCenter) this.entityManager.createQuery(str,
					DeliveryCenter.class).setFlushMode(FlushModeType.COMMIT)
					.getSingleResult();
		} catch (NoResultException localNoResultException) {
		}
		return null;
	}

	public void persist(DeliveryCenter deliveryCenter) {
		Assert.notNull(deliveryCenter);
		if (deliveryCenter.getIsDefault().booleanValue()) {
			String str = "update DeliveryCenter deliveryCenter set deliveryCenter.isDefault = false where deliveryCenter.isDefault = true";
			this.entityManager.createQuery(str).setFlushMode(
					FlushModeType.COMMIT).executeUpdate();
		}
		super.persist(deliveryCenter);
	}

	public DeliveryCenter merge(DeliveryCenter deliveryCenter) {
		Assert.notNull(deliveryCenter);
		if (deliveryCenter.getIsDefault().booleanValue()) {
			String str = "update DeliveryCenter deliveryCenter set deliveryCenter.isDefault = false where deliveryCenter.isDefault = true and deliveryCenter != :deliveryCenter";
			this.entityManager.createQuery(str).setFlushMode(
					FlushModeType.COMMIT).setParameter("deliveryCenter",
					deliveryCenter).executeUpdate();
		}
		return (DeliveryCenter) super.merge(deliveryCenter);
	}
}