package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.ReturnsDao;
import com.korres.entity.Returns;
import com.korres.service.ReturnsService;
import org.springframework.stereotype.Service;

@Service("returnsServiceImpl")
public class ReturnsServiceImpl extends BaseServiceImpl<Returns, Long>
		implements ReturnsService {
	@Resource(name = "returnsDaoImpl")
	public void setBaseDao(ReturnsDao returnsDao) {
		super.setBaseDao(returnsDao);
	}
}