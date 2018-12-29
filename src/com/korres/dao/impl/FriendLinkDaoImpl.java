package com.korres.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import com.korres.dao.FriendLinkDao;
import com.korres.entity.FriendLink;
import com.korres.entity.FriendLink.FriendLinkType;

import org.springframework.stereotype.Repository;

@Repository("friendLinkDaoImpl")
public class FriendLinkDaoImpl extends BaseDaoImpl<FriendLink, Long> implements
		FriendLinkDao {
	public List<FriendLink> findList(FriendLinkType type) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(FriendLink.class);
		Root localRoot = localCriteriaQuery.from(FriendLink.class);
		localCriteriaQuery.select(localRoot);
		if (type != null)
			localCriteriaQuery.where(localCriteriaBuilder.equal(localRoot
					.get("type"), type));
		localCriteriaQuery.orderBy(new Order[] { localCriteriaBuilder
				.asc(localRoot.get("order")) });
		return this.entityManager.createQuery(localCriteriaQuery).setFlushMode(
				FlushModeType.COMMIT).getResultList();
	}
}