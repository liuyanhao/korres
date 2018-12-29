package com.korres.dao.impl;

import com.korres.dao.BrandDao;
import com.korres.entity.Brand;
import org.springframework.stereotype.Repository;

@Repository("brandDaoImpl")
public class BrandDaoImpl extends BaseDaoImpl<Brand, Long> implements BrandDao {
}