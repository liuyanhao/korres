package com.korres.dao.impl;

import com.korres.dao.SpecificationValueDao;
import com.korres.entity.SpecificationValue;
import org.springframework.stereotype.Repository;

@Repository("specificationValueDaoImpl")
public class SpecificationValueDaoImpl extends
		BaseDaoImpl<SpecificationValue, Long> implements SpecificationValueDao {
}