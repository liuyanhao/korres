package com.korres.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;

import com.korres.dao.ProductCategoryDao;
import com.korres.entity.ProductCategory;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository("productCategoryDaoImpl")
public class ProductCategoryDaoImpl extends BaseDaoImpl<ProductCategory, Long>
		implements ProductCategoryDao {
	public List<ProductCategory> findRoots(Integer count) {
		String str = "select productCategory from ProductCategory productCategory where productCategory.parent is null order by productCategory.order asc";
		TypedQuery localTypedQuery = this.entityManager.createQuery(str,
				ProductCategory.class).setFlushMode(FlushModeType.COMMIT);
		if (count != null)
			localTypedQuery.setMaxResults(count.intValue());
		return localTypedQuery.getResultList();
	}

	public List<ProductCategory> findParents(ProductCategory productCategory,
			Integer count) {
		if ((productCategory == null) || (productCategory.getParent() == null))
			return Collections.emptyList();
		String str = "select productCategory from ProductCategory productCategory where productCategory.id in (:ids) order by productCategory.grade asc";
		TypedQuery localTypedQuery = this.entityManager.createQuery(str,
				ProductCategory.class).setFlushMode(FlushModeType.COMMIT)
				.setParameter("ids", productCategory.getTreePaths());
		if (count != null)
			localTypedQuery.setMaxResults(count.intValue());
		return localTypedQuery.getResultList();
	}

	public List<ProductCategory> findChildren(ProductCategory productCategory,
			Integer count) {
		String str;
		TypedQuery localTypedQuery;
		if (productCategory != null) {
			str = "select productCategory from ProductCategory productCategory where productCategory.treePath like :treePath order by productCategory.order asc";
			localTypedQuery = this.entityManager.createQuery(str,
					ProductCategory.class).setFlushMode(FlushModeType.COMMIT)
					.setParameter("treePath",
							"%," + productCategory.getId() + "," + "%");
		} else {
			str = "select productCategory from ProductCategory productCategory order by productCategory.order asc";
			localTypedQuery = this.entityManager.createQuery(str,
					ProductCategory.class).setFlushMode(FlushModeType.COMMIT);
		}
		if (count != null)
			localTypedQuery.setMaxResults(count.intValue());
		return IIIllIlI(localTypedQuery.getResultList(), productCategory);
	}

	public void persist(ProductCategory productCategory) {
		Assert.notNull(productCategory);
		IIIllIlI(productCategory);
		super.persist(productCategory);
	}

	public ProductCategory merge(ProductCategory productCategory) {
		Assert.notNull(productCategory);
		IIIllIlI(productCategory);
		Iterator localIterator = findChildren(productCategory, null).iterator();
		while (localIterator.hasNext()) {
			ProductCategory localProductCategory = (ProductCategory) localIterator
					.next();
			IIIllIlI(localProductCategory);
		}
		return (ProductCategory) super.merge(productCategory);
	}

	public void remove(ProductCategory productCategory) {
		if (productCategory != null) {
			StringBuffer localStringBuffer = new StringBuffer(
					"update Product product set ");
			for (int i = 0; i < 20; i++) {
				String str = "attributeValue" + i;
				if (i == 0)
					localStringBuffer.append("product." + str + " = null");
				else
					localStringBuffer.append(", product." + str + " = null");
			}
			localStringBuffer
					.append(" where product.productCategory = :productCategory");
			this.entityManager.createQuery(localStringBuffer.toString())
					.setFlushMode(FlushModeType.COMMIT).setParameter(
							"productCategory", productCategory).executeUpdate();
			super.remove(productCategory);
		}
	}

	private List<ProductCategory> IIIllIlI(List<ProductCategory> paramList,
			ProductCategory paramProductCategory) {
		ArrayList localArrayList = new ArrayList();
		if (paramList != null) {
			Iterator localIterator = paramList.iterator();
			while (localIterator.hasNext()) {
				ProductCategory localProductCategory = (ProductCategory) localIterator
						.next();
				if (localProductCategory.getParent() == paramProductCategory) {
					localArrayList.add(localProductCategory);
					localArrayList.addAll(IIIllIlI(paramList,
							localProductCategory));
				}
			}
		}
		return localArrayList;
	}

	private void IIIllIlI(ProductCategory paramProductCategory) {
		if (paramProductCategory == null)
			return;
		ProductCategory localProductCategory = paramProductCategory.getParent();
		if (localProductCategory != null)
			paramProductCategory.setTreePath(localProductCategory.getTreePath()
					+ localProductCategory.getId() + ",");
		else
			paramProductCategory.setTreePath(",");
		paramProductCategory.setGrade(Integer.valueOf(paramProductCategory
				.getTreePaths().size()));
	}
}