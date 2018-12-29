package com.korres.service;

import java.util.List;

import com.korres.LogConfig;

public abstract interface LogConfigService {
	public abstract List<LogConfig> getAll();
}