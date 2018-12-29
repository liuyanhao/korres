package com.korres.dao;

import java.util.List;
import com.korres.entity.Navigation;
import com.korres.entity.Navigation.NavigationPosition;

public abstract interface NavigationDao extends BaseDao<Navigation, Long> {
	public abstract List<Navigation> findList(NavigationPosition paramPosition);
}