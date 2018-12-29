package com.korres.dao.impl;

import com.korres.dao.ReturnsDao;
import com.korres.entity.Returns;
import org.springframework.stereotype.Repository;

@Repository("returnsDaoImpl")
public class ReturnsDaoImpl extends BaseDaoImpl<Returns, Long> implements
		ReturnsDao {
}