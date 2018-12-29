package com.korres.dao.impl;

import com.korres.dao.RefundsDao;
import com.korres.entity.Refunds;
import org.springframework.stereotype.Repository;

@Repository("refundsDaoImpl")
public class RefundsDaoImpl extends BaseDaoImpl<Refunds, Long> implements
		RefundsDao {
}