package com.korres.service;

import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Deposit;
import com.korres.entity.Member;

/*
 * 类名：DepositService.java
 * 功能说明：预存款service接口
 * 创建日期：2018-12-20 下午04:56:52
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public abstract interface DepositService extends BaseService<Deposit, Long> {
	public abstract Page<Deposit> findPage(Member paramMember,
			Pageable paramPageable);
}