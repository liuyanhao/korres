package com.korres.service;

import com.korres.entity.Log;

public abstract interface LogService extends BaseService<Log, Long> {
	public abstract void clear();
}