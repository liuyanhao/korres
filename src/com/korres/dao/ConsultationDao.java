package com.korres.dao;

import java.util.List;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Consultation;
import com.korres.entity.Member;
import com.korres.entity.Product;

/*
 * 类名：ConsultationDao.java
 * 功能说明：商品咨询dao接口
 * 创建日期：2018-12-20 下午05:02:07
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public abstract interface ConsultationDao extends BaseDao<Consultation, Long> {
	public abstract List<Consultation> findList(Member paramMember,
			Product paramProduct, Boolean paramBoolean, Integer paramInteger,
			List<Filter> paramList, List<Order> paramList1);

	public abstract Page<Consultation> findPage(Member paramMember,
			Product paramProduct, Boolean paramBoolean, Pageable paramPageable);

	public abstract Long count(Member paramMember, Product paramProduct,
			Boolean paramBoolean);
}