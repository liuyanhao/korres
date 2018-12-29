package com.korres.dao.impl;

import com.korres.dao.RoleDao;
import com.korres.entity.Role;
import org.springframework.stereotype.Repository;

@Repository("roleDaoImpl")
public class RoleDaoImpl extends BaseDaoImpl<Role, Long> implements RoleDao {
}