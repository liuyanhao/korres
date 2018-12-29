package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.DepositDao;
import com.korres.entity.Deposit;
import com.korres.entity.Member;
import com.korres.service.DepositService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Page;
import com.korres.Pageable;

/*
 * 类名：DepositServiceImpl.java
 * 功能说明：预存款service实现类
 * 创建日期：2013-12-20 下午04:57:32
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Service("depositServiceImpl")
public class DepositServiceImpl extends BaseServiceImpl<Deposit, Long>
		implements DepositService {

	@Resource(name = "depositDaoImpl")
	private DepositDao depositDao;

	@Resource(name = "depositDaoImpl")
	public void setBaseDao(DepositDao depositDao) {
		super.setBaseDao(depositDao);
	}

	@Transactional(readOnly = true)
	public Page<Deposit> findPage(Member member, Pageable pageable) {
		return this.depositDao.findPage(member, pageable);
	}
}