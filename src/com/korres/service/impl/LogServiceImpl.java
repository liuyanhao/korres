package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.LogDao;
import com.korres.entity.Log;
import com.korres.service.LogService;
import org.springframework.stereotype.Service;

@Service("logServiceImpl")
public class LogServiceImpl extends BaseServiceImpl<Log, Long> implements
		LogService {

	@Resource(name = "logDaoImpl")
	private LogDao logDao;

	@Resource(name = "logDaoImpl")
	public void setBaseDao(LogDao logDao) {
		super.setBaseDao(logDao);
	}

	public void clear() {
		this.logDao.removeAll();
	}
}