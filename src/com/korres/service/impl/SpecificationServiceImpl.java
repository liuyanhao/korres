package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.SpecificationDao;
import com.korres.entity.Specification;
import com.korres.service.SpecificationService;
import org.springframework.stereotype.Service;

@Service("specificationServiceImpl")
public class SpecificationServiceImpl extends
		BaseServiceImpl<Specification, Long> implements SpecificationService {
	@Resource(name = "specificationDaoImpl")
	public void setBaseDao(SpecificationDao specificationDao) {
		super.setBaseDao(specificationDao);
	}
}