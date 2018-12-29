package com.korres.dao;

import java.util.List;
import java.util.Set;
import com.korres.entity.Parameter;
import com.korres.entity.ParameterGroup;

public abstract interface ParameterDao extends BaseDao<Parameter, Long> {
	public abstract List<Parameter> findList(
			ParameterGroup paramParameterGroup, Set<Parameter> paramSet);
}