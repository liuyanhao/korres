package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.ParameterGroupDao;
import com.korres.entity.ParameterGroup;
import com.korres.service.ParameterGroupService;
import org.springframework.stereotype.Service;

@Service("parameterGroupServiceImpl")
public class ParameterGroupServiceImpl extends
		BaseServiceImpl<ParameterGroup, Long> implements ParameterGroupService {
	@Resource(name = "parameterGroupDaoImpl")
	public void setBaseDao(ParameterGroupDao parameterGroupDao) {
		super.setBaseDao(parameterGroupDao);
	}
}