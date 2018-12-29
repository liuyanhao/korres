package com.korres.dao;

import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Deposit;
import com.korres.entity.Member;

/*
 * 类名：DepositDao.java
 * 功能说明：预存款dao接口
 * 创建日期：2018-12-20 下午04:58:40
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public abstract interface DepositDao extends BaseDao<Deposit, Long> {
	public abstract Page<Deposit> findPage(Member paramMember,
			Pageable paramPageable);
}