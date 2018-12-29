package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.AttributeDao;
import com.korres.entity.Attribute;
import com.korres.service.AttributeService;
import org.springframework.stereotype.Service;

@Service("attributeServiceImpl")
public class AttributeServiceImpl extends BaseServiceImpl<Attribute, Long>
		implements AttributeService {
	@Resource(name = "attributeDaoImpl")
	public void setBaseDao(AttributeDao attributeDao) {
		super.setBaseDao(attributeDao);
	}
}