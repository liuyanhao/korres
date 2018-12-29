package com.korres.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import com.korres.dao.MemberRankDao;
import com.korres.entity.MemberRank;
import com.korres.entity.Product;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository("memberRankDaoImpl")
public class MemberRankDaoImpl extends BaseDaoImpl<MemberRank, Long> implements
		MemberRankDao {
	public boolean nameExists(String name) {
		if (name == null)
			return false;
		String str = "select count(*) from MemberRank memberRank where lower(memberRank.name) = lower(:name)";
		Long localLong = (Long) this.entityManager.createQuery(str, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("name", name)
				.getSingleResult();
		return localLong.longValue() > 0L;
	}

	public boolean amountExists(BigDecimal amount) {
		if (amount == null)
			return false;
		String str = "select count(*) from MemberRank memberRank where memberRank.amount = :amount";
		Long localLong = (Long) this.entityManager.createQuery(str, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("amount",
						amount).getSingleResult();
		return localLong.longValue() > 0L;
	}

	public MemberRank findDefault() {
		try {
			String str = "select memberRank from MemberRank memberRank where memberRank.isDefault = true";
			return (MemberRank) this.entityManager.createQuery(str,
					MemberRank.class).setFlushMode(FlushModeType.COMMIT)
					.getSingleResult();
		} catch (NoResultException localNoResultException) {
		}
		return null;
	}

	public MemberRank findByAmount(BigDecimal amount) {
		if (amount == null)
			return null;
		String str = "select memberRank from MemberRank memberRank where memberRank.isSpecial = false and memberRank.amount <= :amount order by memberRank.amount desc";
		return (MemberRank) this.entityManager.createQuery(str,
				MemberRank.class).setFlushMode(FlushModeType.COMMIT)
				.setParameter("amount", amount).setMaxResults(1)
				.getSingleResult();
	}

	public void persist(MemberRank memberRank) {
		Assert.notNull(memberRank);
		if (memberRank.getIsDefault().booleanValue()) {
			String str = "update MemberRank memberRank set memberRank.isDefault = false where memberRank.isDefault = true";
			this.entityManager.createQuery(str).setFlushMode(
					FlushModeType.COMMIT).executeUpdate();
		}
		super.persist(memberRank);
	}

	public MemberRank merge(MemberRank memberRank) {
		Assert.notNull(memberRank);
		if (memberRank.getIsDefault().booleanValue()) {
			String str = "update MemberRank memberRank set memberRank.isDefault = false where memberRank.isDefault = true and memberRank != :memberRank";
			this.entityManager.createQuery(str).setFlushMode(
					FlushModeType.COMMIT)
					.setParameter("memberRank", memberRank).executeUpdate();
		}
		return (MemberRank) super.merge(memberRank);
	}

	public void remove(MemberRank memberRank) {
		if ((memberRank != null) && (!memberRank.getIsDefault().booleanValue())) {
			String str = "select product from Product product join product.memberPrice memberPrice where index(memberPrice) = :memberRank";
			List localList = this.entityManager.createQuery(str, Product.class)
					.setFlushMode(FlushModeType.COMMIT).setParameter(
							"memberRank", memberRank).getResultList();
			for (int i = 0; i < localList.size(); i++) {
				Product localProduct = (Product) localList.get(i);
				localProduct.getMemberPrice().remove(memberRank);
				if (i % 20 == 0) {
					super.flush();
					super.clear();
				}
			}
			super.remove((MemberRank) super.merge(memberRank));
		}
	}
}