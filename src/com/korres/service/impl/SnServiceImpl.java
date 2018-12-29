package com.korres.service.impl;

import javax.annotation.Resource;

import com.korres.dao.SnDao;
import com.korres.entity.Sn.SnType;
import com.korres.service.SnService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("snServiceImpl")
public class SnServiceImpl implements SnService {

	@Resource(name = "snDaoImpl")
	private SnDao snDao;

	@Transactional
	public String generate(SnType type) {
		return this.snDao.generate(type);
	}
}