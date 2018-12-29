package com.korres.dao.impl;

import com.korres.dao.SpecificationDao;
import com.korres.entity.Specification;
import org.springframework.stereotype.Repository;

@Repository("specificationDaoImpl")
public class SpecificationDaoImpl extends BaseDaoImpl<Specification, Long>
		implements SpecificationDao {
}