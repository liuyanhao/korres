package com.korres.dao;

import com.korres.entity.Log;

public abstract interface LogDao extends BaseDao<Log, Long> {
	public abstract void removeAll();
}