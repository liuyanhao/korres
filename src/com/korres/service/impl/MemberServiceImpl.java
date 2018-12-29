package com.korres.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.LockModeType;
import javax.servlet.http.HttpServletRequest;

import com.korres.dao.DepositDao;
import com.korres.dao.MemberDao;
import com.korres.entity.Admin;
import com.korres.entity.Deposit;
import com.korres.entity.Member;
import com.korres.entity.Deposit.DepositType;
import com.korres.service.MemberService;
import com.korres.util.SettingUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.korres.Page;
import com.korres.Pageable;
import com.korres.Principal;
import com.korres.Setting;

@Service("memberServiceImpl")
public class MemberServiceImpl extends BaseServiceImpl<Member, Long> implements
		MemberService {

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;

	@Resource(name = "depositDaoImpl")
	private DepositDao depositDao;

	@Resource(name = "memberDaoImpl")
	public void setBaseDao(MemberDao memberDao) {
		super.setBaseDao(memberDao);
	}

	@Transactional(readOnly = true)
	public boolean usernameExists(String username) {
		return this.memberDao.usernameExists(username);
	}

	@Transactional(readOnly = true)
	public boolean usernameDisabled(String username) {
		Assert.hasText(username);
		Setting setting = SettingUtils.get();
		if (setting.getDisabledUsernames() != null) {
			for (String str : setting.getDisabledUsernames()) {
				if (StringUtils.containsIgnoreCase(username, str)) {
					return true;
				}
			}
		}

		return false;
	}

	@Transactional(readOnly = true)
	public boolean emailExists(String email) {
		return this.memberDao.emailExists(email);
	}

	@Transactional(readOnly = true)
	public boolean emailUnique(String previousEmail, String currentEmail) {
		if (StringUtils.equalsIgnoreCase(previousEmail, currentEmail)) {
			return true;
		}

		return !this.memberDao.emailExists(currentEmail);
	}

	public void save(Member member, Admin operator) {
		Assert.notNull(member);
		this.memberDao.persist(member);
		if (member.getBalance().compareTo(new BigDecimal(0)) > 0) {
			Deposit deposit = new Deposit();
			deposit.setType(operator != null ? DepositType.adminRecharge
					: DepositType.memberRecharge);
			deposit.setCredit(member.getBalance());
			deposit.setDebit(new BigDecimal(0));
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername()
					: null);
			deposit.setMember(member);

			this.depositDao.persist(deposit);
		}
	}

	public void update(Member member, Integer modifyPoint,
			BigDecimal modifyBalance, String depositMemo, Admin operator) {
		Assert.notNull(member);
		this.memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
		if ((modifyPoint != null)
				&& (modifyPoint.intValue() != 0)
				&& (member.getPoint().longValue() + modifyPoint.intValue() >= 0L)) {
			member.setPoint(Long.valueOf(member.getPoint().longValue()
					+ modifyPoint.intValue()));
		}

		if ((modifyBalance != null)
				&& (modifyBalance.compareTo(new BigDecimal(0)) != 0)
				&& (member.getBalance().add(modifyBalance).compareTo(
						new BigDecimal(0)) >= 0)) {
			member.setBalance(member.getBalance().add(modifyBalance));
			Deposit deposit = new Deposit();
			if (modifyBalance.compareTo(new BigDecimal(0)) > 0) {
				deposit.setType(operator != null ? DepositType.adminRecharge
						: DepositType.memberRecharge);
				deposit.setCredit(modifyBalance);
				deposit.setDebit(new BigDecimal(0));
			} else {
				deposit.setType(operator != null ? DepositType.adminChargeback
						: DepositType.memberPayment);
				deposit.setCredit(new BigDecimal(0));
				deposit.setDebit(modifyBalance);
			}

			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername()
					: null);
			deposit.setMemo(depositMemo);
			deposit.setMember(member);
			this.depositDao.persist(deposit);
		}

		this.memberDao.merge(member);
	}

	@Transactional(readOnly = true)
	public Member findByUsername(String username) {
		return this.memberDao.findByUsername(username);
	}

	@Transactional(readOnly = true)
	public List<Member> findListByEmail(String email) {
		return this.memberDao.findListByEmail(email);
	}

	@Transactional(readOnly = true)
	public Page<Object> findPurchasePage(Date beginDate, Date endDate,
			Pageable pageable) {
		return this.memberDao.findPurchasePage(beginDate, endDate, pageable);
	}

	@Transactional(readOnly = true)
	public boolean isAuthenticated() {
		RequestAttributes requestAttributes = RequestContextHolder
				.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes)
					.getRequest();
			Principal principal = (Principal) httpServletRequest.getSession()
					.getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly = true)
	public Member getCurrent() {
		RequestAttributes requestAttributes = RequestContextHolder
				.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes)
					.getRequest();
			Principal principal = (Principal) httpServletRequest.getSession()
					.getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return this.memberDao.find(principal.getId());
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	public String getCurrentUsername() {
		RequestAttributes requestAttributes = RequestContextHolder
				.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes)
					.getRequest();
			Principal localPrincipal = (Principal) httpServletRequest
					.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			if (localPrincipal != null) {
				return localPrincipal.getUsername();
			}
		}

		return null;
	}
}