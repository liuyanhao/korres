package com.korres.dao.impl;

import com.korres.dao.ShippingMethodDao;
import com.korres.entity.ShippingMethod;
import org.springframework.stereotype.Repository;

@Repository("shippingMethodDaoImpl")
public class ShippingMethodDaoImpl extends BaseDaoImpl<ShippingMethod, Long>
		implements ShippingMethodDao {
}