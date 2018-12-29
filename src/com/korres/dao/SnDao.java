package com.korres.dao;

import com.korres.entity.Sn.SnType;

public abstract interface SnDao {
	public abstract String generate(SnType paramType);
}