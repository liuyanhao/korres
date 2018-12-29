package com.korres.dao.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.FlushModeType;

import com.korres.dao.ParameterDao;
import com.korres.dao.ParameterGroupDao;
import com.korres.entity.Parameter;
import com.korres.entity.ParameterGroup;
import com.korres.entity.Product;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository("parameterGroupDaoImpl")
public class ParameterGroupDaoImpl extends BaseDaoImpl<ParameterGroup, Long>
		implements ParameterGroupDao {

	@Resource(name = "parameterDaoImpl")
	private ParameterDao IIIllIll;

	public ParameterGroup merge(ParameterGroup parameterGroup) {
		Assert.notNull(parameterGroup);
		HashSet localHashSet = new HashSet();
		CollectionUtils.select(parameterGroup.getParameters(),
				new ParameterGroupDaoImpl$1(this), localHashSet);
		List localList1 = this.IIIllIll.findList(parameterGroup, localHashSet);
		for (int i = 0; i < localList1.size(); i++) {
			Parameter localParameter = (Parameter) localList1.get(i);
			String str = "select product from Product product join product.parameterValue parameterValue where index(parameterValue) = :parameter";
			List localList2 = this.entityManager
					.createQuery(str, Product.class).setFlushMode(
							FlushModeType.COMMIT).setParameter("parameter",
							localParameter).getResultList();
			Iterator localIterator = localList2.iterator();
			while (localIterator.hasNext()) {
				Product localProduct = (Product) localIterator.next();
				localProduct.getParameterValue().remove(localParameter);
				if (i % 20 == 0) {
					super.flush();
					super.clear();
				}
			}
		}
		return (ParameterGroup) super.merge(parameterGroup);
	}

	public void remove(ParameterGroup parameterGroup) {
		if (parameterGroup != null) {
			for (int i = 0; i < parameterGroup.getParameters().size(); i++) {
				Parameter localParameter = (Parameter) parameterGroup
						.getParameters().get(i);
				String str = "select product from Product product join product.parameterValue parameterValue where index(parameterValue) = :parameter";
				List localList = this.entityManager.createQuery(str,
						Product.class).setFlushMode(FlushModeType.COMMIT)
						.setParameter("parameter", localParameter)
						.getResultList();
				Iterator localIterator = localList.iterator();
				while (localIterator.hasNext()) {
					Product localProduct = (Product) localIterator.next();
					localProduct.getParameterValue().remove(localParameter);
					if (i % 20 == 0) {
						super.flush();
						super.clear();
					}
				}
			}
			super.remove((ParameterGroup) super.merge(parameterGroup));
		}
	}
}