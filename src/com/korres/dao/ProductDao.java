package com.korres.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Attribute;
import com.korres.entity.Brand;
import com.korres.entity.Goods;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.entity.Product.ProductOrderType;
import com.korres.entity.ProductCategory;
import com.korres.entity.Promotion;
import com.korres.entity.Tag;

public abstract interface ProductDao extends BaseDao<Product, Long> {
	public abstract boolean snExists(String paramString);

	public abstract Product findBySn(String paramString);

	public abstract List<Product> search(String paramString,
			Boolean paramBoolean, Integer paramInteger);

	public abstract List<Product> findList(
			ProductCategory paramProductCategory, Brand paramBrand,
			Promotion paramPromotion, List<Tag> paramList,
			Map<Attribute, String> paramMap, BigDecimal paramBigDecimal1,
			BigDecimal paramBigDecimal2, Boolean paramBoolean1,
			Boolean paramBoolean2, Boolean paramBoolean3,
			Boolean paramBoolean4, Boolean paramBoolean5,
			Boolean paramBoolean6, ProductOrderType paramOrderType,
			Integer paramInteger, List<Filter> paramList1,
			List<Order> paramList2);

	public abstract List<Product> findList(
			ProductCategory paramProductCategory, Date paramDate1,
			Date paramDate2, Integer paramInteger1, Integer paramInteger2);

	public abstract List<Product> findList(Goods paramGoods,
			Set<Product> paramSet);

	public abstract Page<Product> findPage(
			ProductCategory paramProductCategory, Brand paramBrand,
			Promotion paramPromotion, List<Tag> paramList,
			Map<Attribute, String> paramMap, BigDecimal paramBigDecimal1,
			BigDecimal paramBigDecimal2, Boolean paramBoolean1,
			Boolean paramBoolean2, Boolean paramBoolean3,
			Boolean paramBoolean4, Boolean paramBoolean5,
			Boolean paramBoolean6, ProductOrderType paramOrderType,
			Pageable paramPageable);

	public abstract Page<Product> findPage(Member paramMember,
			Pageable paramPageable);

	public abstract Page<Object> findSalesPage(Date paramDate1,
			Date paramDate2, Pageable paramPageable);

	public abstract Long count(Member paramMember, Boolean paramBoolean1,
			Boolean paramBoolean2, Boolean paramBoolean3,
			Boolean paramBoolean4, Boolean paramBoolean5, Boolean paramBoolean6);

	public abstract boolean isPurchased(Member paramMember, Product paramProduct);
}