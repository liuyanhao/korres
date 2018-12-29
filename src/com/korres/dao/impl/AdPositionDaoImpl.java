package com.korres.dao.impl;

import com.korres.dao.AdPositionDao;
import com.korres.entity.AdPosition;
import org.springframework.stereotype.Repository;

@Repository("adPositionDaoImpl")
public class AdPositionDaoImpl extends BaseDaoImpl<AdPosition, Long> implements
		AdPositionDao {
}