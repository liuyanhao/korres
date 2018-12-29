package com.korres.service;

import com.korres.entity.Sn.SnType;

public abstract interface SnService {
	public abstract String generate(SnType paramType);
}