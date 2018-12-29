package com.korres.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.korres.dao.ReceiverDao;
import com.korres.entity.Member;
import com.korres.entity.Receiver;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.korres.Page;
import com.korres.Pageable;

@Repository("receiverDaoImpl")
public class ReceiverDaoImpl extends BaseDaoImpl<Receiver, Long> implements
		ReceiverDao {
	public Receiver findDefault(Member member) {
		if (member == null)
			return null;
		try {
			String sql = "select receiver from Receiver receiver where receiver.member = :member and receiver.isDefault = true";
			return (Receiver) this.entityManager.createQuery(sql,
					Receiver.class).setFlushMode(FlushModeType.COMMIT)
					.setParameter("member", member).getSingleResult();
		} catch (NoResultException e) {
			try {
				String sql = "select receiver from Receiver receiver where receiver.member = :member order by receiver.modifyDate desc";
				return (Receiver) this.entityManager.createQuery(sql,
						Receiver.class).setFlushMode(FlushModeType.COMMIT)
						.setParameter("member", member).setMaxResults(1)
						.getSingleResult();
			} catch (NoResultException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public Page<Receiver> findPage(Member member, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder
				.createQuery(Receiver.class);
		Root root = criteriaQuery.from(Receiver.class);
		criteriaQuery.select(root);
		if (member != null){
			criteriaQuery.where(criteriaBuilder.equal(root
					.get("member"), member));
		}
		
		return super.findPage(criteriaQuery, pageable);
	}

	public void persist(Receiver receiver) {
		Assert.notNull(receiver);
		Assert.notNull(receiver.getMember());
		if (receiver.getIsDefault().booleanValue()) {
			String sql = "update Receiver receiver set receiver.isDefault = false where receiver.member = :member and receiver.isDefault = true";
			this.entityManager.createQuery(sql).setFlushMode(
					FlushModeType.COMMIT).setParameter("member",
					receiver.getMember()).executeUpdate();
		}
		super.persist(receiver);
	}

	public Receiver merge(Receiver receiver) {
		Assert.notNull(receiver);
		Assert.notNull(receiver.getMember());
		if (receiver.getIsDefault().booleanValue()) {
			String sql = "update Receiver receiver set receiver.isDefault = false where receiver.member = :member and receiver.isDefault = true and receiver != :receiver";
			this.entityManager.createQuery(sql).setFlushMode(
					FlushModeType.COMMIT).setParameter("member",
					receiver.getMember()).setParameter("receiver", receiver)
					.executeUpdate();
		}
		return (Receiver) super.merge(receiver);
	}
}