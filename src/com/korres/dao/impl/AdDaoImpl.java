package com.korres.dao.impl;

import com.korres.dao.AdDao;
import com.korres.entity.Ad;
import org.springframework.stereotype.Repository;

@Repository("adDaoImpl")
public class AdDaoImpl extends BaseDaoImpl<Ad, Long> implements AdDao {
}