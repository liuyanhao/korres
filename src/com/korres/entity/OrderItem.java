package com.korres.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="xx_order_item")
public class OrderItem extends BaseEntity
{
  private static final long serialVersionUID = -4999926022604479334L;
  private String sn;
  private String name;
  private String fullName;
  private BigDecimal price;
  private Integer weight;
  private String thumbnail;
  private Boolean isGift;
  private Integer quantity;
  private Integer shippedQuantity;
  private Integer returnQuantity;
  private Product product;
  private Order order;

  @JsonProperty
  @NotEmpty
  @Column(nullable=false, updatable=false)
  public String getSn()
  {
    return this.sn;
  }

  public void setSn(String sn)
  {
    this.sn = sn;
  }

  @JsonProperty
  @Column(nullable=false, updatable=false)
  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  @JsonProperty
  @Column(nullable=false, updatable=false)
  public String getFullName()
  {
    return this.fullName;
  }

  public void setFullName(String fullName)
  {
    this.fullName = fullName;
  }

  @JsonProperty
  @NotNull
  @Min(0L)
  @Digits(integer=12, fraction=3)
  @Column(nullable=false, precision=21, scale=6)
  public BigDecimal getPrice()
  {
    return this.price;
  }

  public void setPrice(BigDecimal price)
  {
    this.price = price;
  }

  @JsonProperty
  @Column(updatable=false)
  public Integer getWeight()
  {
    return this.weight;
  }

  public void setWeight(Integer weight)
  {
    this.weight = weight;
  }

  @JsonProperty
  @Column(updatable=false)
  public String getThumbnail()
  {
    return this.thumbnail;
  }

  public void setThumbnail(String thumbnail)
  {
    this.thumbnail = thumbnail;
  }

  @JsonProperty
  @Column(nullable=false, updatable=false)
  public Boolean getIsGift()
  {
    return this.isGift;
  }

  public void setIsGift(Boolean isGift)
  {
    this.isGift = isGift;
  }

  @JsonProperty
  @NotNull
  @Min(1L)
  @Max(10000L)
  @Column(nullable=false)
  public Integer getQuantity()
  {
    return this.quantity;
  }

  public void setQuantity(Integer quantity)
  {
    this.quantity = quantity;
  }

  @Column(nullable=false)
  public Integer getShippedQuantity()
  {
    return this.shippedQuantity;
  }

  public void setShippedQuantity(Integer shippedQuantity)
  {
    this.shippedQuantity = shippedQuantity;
  }

  @Column(nullable=false)
  public Integer getReturnQuantity()
  {
    return this.returnQuantity;
  }

  public void setReturnQuantity(Integer returnQuantity)
  {
    this.returnQuantity = returnQuantity;
  }

  @ManyToOne(fetch=FetchType.LAZY)
  public Product getProduct()
  {
    return this.product;
  }

  public void setProduct(Product product)
  {
    this.product = product;
  }

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="orders", nullable=false, updatable=false)
  public Order getOrder()
  {
    return this.order;
  }

  public void setOrder(Order order)
  {
    this.order = order;
  }

  @JsonProperty
  @Transient
  public int getTotalWeight()
  {
    if ((getWeight() != null) && (getQuantity() != null))
      return getWeight().intValue() * getQuantity().intValue();
    return 0;
  }

  @JsonProperty
  @Transient
  public BigDecimal getSubtotal()
  {
    if ((getPrice() != null) && (getQuantity() != null))
      return getPrice().multiply(new BigDecimal(getQuantity().intValue()));
    return new BigDecimal(0);
  }
}