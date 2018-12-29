package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.RefundsDao;
import com.korres.entity.Refunds;
import com.korres.service.RefundsService;
import org.springframework.stereotype.Service;

@Service("refundsServiceImpl")
public class RefundsServiceImpl extends BaseServiceImpl<Refunds, Long>
		implements RefundsService {
	@Resource(name = "refundsDaoImpl")
	public void setBaseDao(RefundsDao refundsDao) {
		super.setBaseDao(refundsDao);
	}
}