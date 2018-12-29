package com.korres.dao.impl;

import com.korres.dao.DeliveryCorpDao;
import com.korres.entity.DeliveryCorp;
import org.springframework.stereotype.Repository;

@Repository("deliveryCorpDaoImpl")
public class DeliveryCorpDaoImpl extends BaseDaoImpl<DeliveryCorp, Long>
		implements DeliveryCorpDao {
}