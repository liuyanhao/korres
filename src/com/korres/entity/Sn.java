package com.korres.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/*
 * 类名：Sn.java
 * 功能说明：
 * 创建日期：2013-8-28 下午03:39:21
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Entity
@Table(name="xx_sn")
public class Sn extends BaseEntity
{
  private static final long serialVersionUID = -2330598144835706164L;
  private SnType type;
  private Long lastValue;

  @Column(nullable=false, updatable=false, unique=true)
  public SnType getType()
  {
    return this.type;
  }

  public void setType(SnType type)
  {
    this.type = type;
  }

  @Column(nullable=false)
  public Long getLastValue()
  {
    return this.lastValue;
  }

  public void setLastValue(Long lastValue)
  {
    this.lastValue = lastValue;
  }
  
  public enum SnType
  {
    product, order, payment, refunds, shipping, returns;
  }
}