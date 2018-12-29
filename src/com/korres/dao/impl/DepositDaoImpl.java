package com.korres.dao.impl;

import java.util.Collections;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.korres.Page;
import com.korres.Pageable;
import com.korres.dao.DepositDao;
import com.korres.entity.Deposit;
import com.korres.entity.Member;

/*
 * 类名：DepositDaoImpl.java
 * 功能说明：预存款dao实现类
 * 创建日期：2013-12-20 下午04:58:23
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Repository("depositDaoImpl")
public class DepositDaoImpl extends BaseDaoImpl<Deposit, Long> implements
		DepositDao {
	public Page<Deposit> findPage(Member member, Pageable pageable) {
		if (member == null){
			return new Page(Collections.emptyList(), 0L, pageable);
		}
		
		CriteriaBuilder criteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(Deposit.class);
		Root root = criteriaQuery.from(Deposit.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("member"), member));
		
		return super.findPage(criteriaQuery, pageable);
	}
}