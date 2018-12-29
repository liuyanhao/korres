package com.korres.entity;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.korres.entity.MemberAttribute.MemberAttributeType;
import com.korres.interceptor.MemberInterceptor;
import com.korres.util.JsonUtils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：Member.java
 * 功能说明：会员实体类
 * 创建日期：2013-8-28 下午03:02:33
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_member")
public class Member extends BaseEntity {
	private static final long serialVersionUID = 1533130686714725835L;
	public static final String PRINCIPAL_ATTRIBUTE_NAME = MemberInterceptor.class
			.getName()
			+ ".PRINCIPAL";
	public static final String USERNAME_COOKIE_NAME = "username";
	public static final int ATTRIBUTE_VALUE_PROPERTY_COUNT = 10;
	public static final String ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX = "attributeValue";
	public static final Integer MAX_FAVORITE_COUNT = Integer.valueOf(10);
	private String username;
	private String password;
	private String email;
	private Long point;
	private BigDecimal amount;
	private BigDecimal balance;
	private Boolean isEnabled;
	private Boolean isLocked;
	private Integer loginFailureCount;
	private Date lockedDate;
	private String registerIp;
	private String loginIp;
	private Date loginDate;
	private String name;
	private MemberGender gender;
	private Date birth;
	private String address;
	private String zipCode;
	private String phone;
	private String mobile;
	private String attributeValue0;
	private String attributeValue1;
	private String attributeValue2;
	private String attributeValue3;
	private String attributeValue4;
	private String attributeValue5;
	private String attributeValue6;
	private String attributeValue7;
	private String attributeValue8;
	private String attributeValue9;
	private SafeKey safeKey;
	private Area area;
	private MemberRank memberRank;
	private Cart cart;
	private Set<Order> orders = new HashSet<Order>();
	private Set<Deposit> deposits = new HashSet<Deposit>();
	private Set<Payment> payments = new HashSet<Payment>();
	private Set<CouponCode> couponCodes = new HashSet<CouponCode>();
	private Set<Receiver> receivers = new HashSet<Receiver>();
	private Set<Review> reviews = new HashSet<Review>();
	private Set<Consultation> consultations = new HashSet<Consultation>();
	private Set<Product> favoriteProducts = new HashSet<Product>();
	private Set<ProductNotify> productNotifies = new HashSet<ProductNotify>();
	private Set<Message> inMessages = new HashSet<Message>();
	private Set<Message> outMessages = new HashSet<Message>();

	@NotEmpty(groups = { BaseEntitySave.class })
	@Pattern(regexp = "^[0-9a-z_A-Z\\u4e00-\\u9fa5]+$")
	@Column(nullable = false, updatable = false, unique = true)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@NotEmpty(groups = { BaseEntitySave.class })
	@Pattern(regexp = "^[^\\s&\"<>]+$")
	@Column(nullable = false)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@NotEmpty
	@Email
	@Length(max = 200)
	@Column(nullable = false)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@NotNull(groups = { BaseEntitySave.class })
	@Min(0L)
	@Column(nullable = false)
	public Long getPoint() {
		return this.point;
	}

	public void setPoint(Long point) {
		this.point = point;
	}

	@Column(nullable = false, precision = 27, scale = 12)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@NotNull(groups = { BaseEntitySave.class })
	@Min(0L)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 27, scale = 12)
	public BigDecimal getBalance() {
		return this.balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@NotNull
	@Column(nullable = false)
	public Boolean getIsEnabled() {
		return this.isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Column(nullable = false)
	public Boolean getIsLocked() {
		return this.isLocked;
	}

	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}

	@Column(nullable = false)
	public Integer getLoginFailureCount() {
		return this.loginFailureCount;
	}

	public void setLoginFailureCount(Integer loginFailureCount) {
		this.loginFailureCount = loginFailureCount;
	}

	public Date getLockedDate() {
		return this.lockedDate;
	}

	public void setLockedDate(Date lockedDate) {
		this.lockedDate = lockedDate;
	}

	@Column(nullable = false, updatable = false)
	public String getRegisterIp() {
		return this.registerIp;
	}

	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}

	public String getLoginIp() {
		return this.loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public Date getLoginDate() {
		return this.loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	@Length(max = 200)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MemberGender getGender() {
		return this.gender;
	}

	public void setGender(MemberGender gender) {
		this.gender = gender;
	}

	public Date getBirth() {
		return this.birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	@Length(max = 200)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Length(max = 200)
	public String getZipCode() {
		return this.zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Length(max = 200)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Length(max = 200)
	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Length(max = 200)
	public String getAttributeValue0() {
		return this.attributeValue0;
	}

	public void setAttributeValue0(String attributeValue0) {
		this.attributeValue0 = attributeValue0;
	}

	@Length(max = 200)
	public String getAttributeValue1() {
		return this.attributeValue1;
	}

	public void setAttributeValue1(String attributeValue1) {
		this.attributeValue1 = attributeValue1;
	}

	@Length(max = 200)
	public String getAttributeValue2() {
		return this.attributeValue2;
	}

	public void setAttributeValue2(String attributeValue2) {
		this.attributeValue2 = attributeValue2;
	}

	@Length(max = 200)
	public String getAttributeValue3() {
		return this.attributeValue3;
	}

	public void setAttributeValue3(String attributeValue3) {
		this.attributeValue3 = attributeValue3;
	}

	@Length(max = 200)
	public String getAttributeValue4() {
		return this.attributeValue4;
	}

	public void setAttributeValue4(String attributeValue4) {
		this.attributeValue4 = attributeValue4;
	}

	@Length(max = 200)
	public String getAttributeValue5() {
		return this.attributeValue5;
	}

	public void setAttributeValue5(String attributeValue5) {
		this.attributeValue5 = attributeValue5;
	}

	@Length(max = 200)
	public String getAttributeValue6() {
		return this.attributeValue6;
	}

	public void setAttributeValue6(String attributeValue6) {
		this.attributeValue6 = attributeValue6;
	}

	@Length(max = 200)
	public String getAttributeValue7() {
		return this.attributeValue7;
	}

	public void setAttributeValue7(String attributeValue7) {
		this.attributeValue7 = attributeValue7;
	}

	@Length(max = 200)
	public String getAttributeValue8() {
		return this.attributeValue8;
	}

	public void setAttributeValue8(String attributeValue8) {
		this.attributeValue8 = attributeValue8;
	}

	@Length(max = 200)
	public String getAttributeValue9() {
		return this.attributeValue9;
	}

	public void setAttributeValue9(String attributeValue9) {
		this.attributeValue9 = attributeValue9;
	}

	@Embedded
	public SafeKey getSafeKey() {
		return this.safeKey;
	}

	public void setSafeKey(SafeKey safeKey) {
		this.safeKey = safeKey;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Area getArea() {
		return this.area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public MemberRank getMemberRank() {
		return this.memberRank;
	}

	public void setMemberRank(MemberRank memberRank) {
		this.memberRank = memberRank;
	}

	@OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	public Cart getCart() {
		return this.cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	public Set<Order> getOrders() {
		return this.orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	public Set<Deposit> getDeposits() {
		return this.deposits;
	}

	public void setDeposits(Set<Deposit> deposits) {
		this.deposits = deposits;
	}

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	public Set<Payment> getPayments() {
		return this.payments;
	}

	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	public Set<CouponCode> getCouponCodes() {
		return this.couponCodes;
	}

	public void setCouponCodes(Set<CouponCode> couponCodes) {
		this.couponCodes = couponCodes;
	}

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	@OrderBy("isDefault desc, createDate desc")
	public Set<Receiver> getReceivers() {
		return this.receivers;
	}

	public void setReceivers(Set<Receiver> receivers) {
		this.receivers = receivers;
	}

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	@OrderBy("createDate desc")
	public Set<Review> getReviews() {
		return this.reviews;
	}

	public void setReviews(Set<Review> reviews) {
		this.reviews = reviews;
	}

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	@OrderBy("createDate desc")
	public Set<Consultation> getConsultations() {
		return this.consultations;
	}

	public void setConsultations(Set<Consultation> consultations) {
		this.consultations = consultations;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_member_favorite_product")
	@OrderBy("createDate desc")
	public Set<Product> getFavoriteProducts() {
		return this.favoriteProducts;
	}

	public void setFavoriteProducts(Set<Product> favoriteProducts) {
		this.favoriteProducts = favoriteProducts;
	}

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	public Set<ProductNotify> getProductNotifies() {
		return this.productNotifies;
	}

	public void setProductNotifies(Set<ProductNotify> productNotifies) {
		this.productNotifies = productNotifies;
	}

	@OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	public Set<Message> getInMessages() {
		return this.inMessages;
	}

	public void setInMessages(Set<Message> inMessages) {
		this.inMessages = inMessages;
	}

	@OneToMany(mappedBy = "sender", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	public Set<Message> getOutMessages() {
		return this.outMessages;
	}

	public void setOutMessages(Set<Message> outMessages) {
		this.outMessages = outMessages;
	}

	@Transient
	public Object getAttributeValue(MemberAttribute memberAttribute) {
		if (memberAttribute != null) {
			if (memberAttribute.getType() == MemberAttributeType.name) {
				return getName();
			}

			if (memberAttribute.getType() == MemberAttributeType.gender) {
				return getGender();
			}

			if (memberAttribute.getType() == MemberAttributeType.birth) {
				return getBirth();
			}

			if (memberAttribute.getType() == MemberAttributeType.area) {
				return getArea();
			}

			if (memberAttribute.getType() == MemberAttributeType.address) {
				return getAddress();
			}

			if (memberAttribute.getType() == MemberAttributeType.zipCode) {
				return getZipCode();
			}

			if (memberAttribute.getType() == MemberAttributeType.phone) {
				return getPhone();
			}

			if (memberAttribute.getType() == MemberAttributeType.mobile) {
				return getMobile();
			}

			if (memberAttribute.getType() == MemberAttributeType.checkbox) {
				if (memberAttribute.getPropertyIndex() != null)
					try {
						String str1 = "attributeValue"
								+ memberAttribute.getPropertyIndex();
						String str3 = (String) PropertyUtils.getProperty(this,
								str1);
						if (str3 == null) {
							return null;
						}

						return JsonUtils.toObject(str3, List.class);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
			} else if (memberAttribute.getPropertyIndex() != null)
				try {
					String str2 = "attributeValue"
							+ memberAttribute.getPropertyIndex();
					return (String) PropertyUtils.getProperty(this, str2);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
		}

		return null;
	}

	@Transient
	public void setAttributeValue(MemberAttribute memberAttribute,
			Object attributeValue) {
		if (memberAttribute != null) {
			if (((attributeValue instanceof String))
					&& (StringUtils.isEmpty((String) attributeValue))) {
				attributeValue = null;
			}

			if ((memberAttribute.getType() == MemberAttributeType.name)
					&& (((attributeValue instanceof String)) || (attributeValue == null))) {
				setName((String) attributeValue);
			} else if ((memberAttribute.getType() == MemberAttributeType.gender)
					&& (((attributeValue instanceof MemberGender)) || (attributeValue == null))) {
				setGender((MemberGender) attributeValue);
			} else if ((memberAttribute.getType() == MemberAttributeType.birth)
					&& (((attributeValue instanceof Date)) || (attributeValue == null))) {
				setBirth((Date) attributeValue);
			} else if ((memberAttribute.getType() == MemberAttributeType.area)
					&& (((attributeValue instanceof Area)) || (attributeValue == null))) {
				setArea((Area) attributeValue);
			} else if ((memberAttribute.getType() == MemberAttributeType.address)
					&& (((attributeValue instanceof String)) || (attributeValue == null))) {
				setAddress((String) attributeValue);
			} else if ((memberAttribute.getType() == MemberAttributeType.zipCode)
					&& (((attributeValue instanceof String)) || (attributeValue == null))) {
				setZipCode((String) attributeValue);
			} else if ((memberAttribute.getType() == MemberAttributeType.phone)
					&& (((attributeValue instanceof String)) || (attributeValue == null))) {
				setPhone((String) attributeValue);
			} else if ((memberAttribute.getType() == MemberAttributeType.mobile)
					&& (((attributeValue instanceof String)) || (attributeValue == null))) {
				setMobile((String) attributeValue);
			} else if ((memberAttribute.getType() == MemberAttributeType.checkbox)
					&& (((attributeValue instanceof List)) || (attributeValue == null))) {
				if ((memberAttribute.getPropertyIndex() != null)
						&& ((attributeValue == null) || ((memberAttribute
								.getOptions() != null) && (memberAttribute
								.getOptions()
								.containsAll((List) attributeValue)))))
					try {
						String str1 = "attributeValue"
								+ memberAttribute.getPropertyIndex();
						PropertyUtils.setProperty(this, str1, JsonUtils
								.toJson(attributeValue));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
			} else if (memberAttribute.getPropertyIndex() != null)
				try {
					String str2 = "attributeValue"
							+ memberAttribute.getPropertyIndex();
					PropertyUtils.setProperty(this, str2, attributeValue);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
		}
	}

	@Transient
	public void removeAttributeValue() {
		setName(null);
		setGender(null);
		setBirth(null);
		setArea(null);
		setAddress(null);
		setZipCode(null);
		setPhone(null);
		setMobile(null);
		for (int i = 0; i < 10; i++) {
			String str = "attributeValue" + i;
			try {
				PropertyUtils.setProperty(this, str, null);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	public enum MemberGender {
		male, female;
	}
}