package com.korres.service;

import java.util.List;
import com.korres.entity.Area;

public abstract interface AreaService extends BaseService<Area, Long> {
	public abstract List<Area> findRoots();

	public abstract List<Area> findRoots(Integer paramInteger);
}