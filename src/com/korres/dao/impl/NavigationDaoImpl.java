package com.korres.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import com.korres.dao.NavigationDao;
import com.korres.entity.Navigation;
import com.korres.entity.Navigation.NavigationPosition;

import org.springframework.stereotype.Repository;

@Repository("navigationDaoImpl")
public class NavigationDaoImpl extends BaseDaoImpl<Navigation, Long> implements
		NavigationDao {
	public List<Navigation> findList(NavigationPosition position) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(Navigation.class);
		Root localRoot = localCriteriaQuery.from(Navigation.class);
		localCriteriaQuery.select(localRoot);
		if (position != null)
			localCriteriaQuery.where(localCriteriaBuilder.equal(localRoot
					.get("position"), position));
		localCriteriaQuery.orderBy(new Order[] { localCriteriaBuilder
				.asc(localRoot.get("order")) });
		return this.entityManager.createQuery(localCriteriaQuery).setFlushMode(
				FlushModeType.COMMIT).getResultList();
	}
}