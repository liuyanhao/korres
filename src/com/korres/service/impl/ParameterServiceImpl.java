package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.ParameterDao;
import com.korres.entity.Parameter;
import com.korres.service.ParameterService;
import org.springframework.stereotype.Service;

@Service("parameterServiceImpl")
public class ParameterServiceImpl extends BaseServiceImpl<Parameter, Long>
		implements ParameterService {
	@Resource(name = "parameterDaoImpl")
	public void setBaseDao(ParameterDao parameterDao) {
		super.setBaseDao(parameterDao);
	}
}