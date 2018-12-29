package com.korres.dao;

import java.util.List;
import com.korres.entity.Area;

public abstract interface AreaDao extends BaseDao<Area, Long> {
	public abstract List<Area> findRoots(Integer paramInteger);
}