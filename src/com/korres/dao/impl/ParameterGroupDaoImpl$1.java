package com.korres.dao.impl;

import com.korres.entity.Parameter;

import org.apache.commons.collections.Predicate;

class ParameterGroupDaoImpl$1
  implements Predicate
{
  ParameterGroupDaoImpl$1(ParameterGroupDaoImpl paramParameterGroupDaoImpl)
  {
  }

  public boolean evaluate(Object object)
  {
    Parameter localParameter = (Parameter)object;
    return (localParameter != null) && (localParameter.getId() != null);
  }
}