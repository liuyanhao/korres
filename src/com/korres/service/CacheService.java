package com.korres.service;

public abstract interface CacheService {
	public abstract String getDiskStorePath();

	public abstract int getCacheSize();

	public abstract void clear();
}