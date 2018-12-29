package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.SpecificationValueDao;
import com.korres.entity.SpecificationValue;
import com.korres.service.SpecificationValueService;
import org.springframework.stereotype.Service;

@Service("specificationValueServiceImpl")
public class SpecificationValueServiceImpl extends
		BaseServiceImpl<SpecificationValue, Long> implements
		SpecificationValueService {
	@Resource(name = "specificationValueDaoImpl")
	public void setBaseDao(SpecificationValueDao specificationValueDao) {
		super.setBaseDao(specificationValueDao);
	}
}