package com.korres.service.impl;

import java.math.BigDecimal;
import javax.annotation.Resource;
import com.korres.dao.MemberRankDao;
import com.korres.entity.MemberRank;
import com.korres.service.MemberRankService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("memberRankServiceImpl")
public class MemberRankServiceImpl extends BaseServiceImpl<MemberRank, Long>
		implements MemberRankService {

	@Resource(name = "memberRankDaoImpl")
	private MemberRankDao memberRankDao;

	@Resource(name = "memberRankDaoImpl")
	public void setBaseDao(MemberRankDao memberRankDao) {
		super.setBaseDao(memberRankDao);
	}

	@Transactional(readOnly = true)
	public boolean nameExists(String name) {
		return this.memberRankDao.nameExists(name);
	}

	@Transactional(readOnly = true)
	public boolean nameUnique(String previousName, String currentName) {
		if (StringUtils.equalsIgnoreCase(previousName, currentName)) {
			return true;
		}

		return !this.memberRankDao.nameExists(currentName);
	}

	@Transactional(readOnly = true)
	public boolean amountExists(BigDecimal amount) {
		return this.memberRankDao.amountExists(amount);
	}

	@Transactional(readOnly = true)
	public boolean amountUnique(BigDecimal previousAmount,
			BigDecimal currentAmount) {
		if ((previousAmount != null)
				&& (previousAmount.compareTo(currentAmount) == 0)) {
			return true;
		}

		return !this.memberRankDao.amountExists(currentAmount);
	}

	@Transactional(readOnly = true)
	public MemberRank findDefault() {
		return this.memberRankDao.findDefault();
	}

	@Transactional(readOnly = true)
	public MemberRank findByAmount(BigDecimal amount) {
		return this.memberRankDao.findByAmount(amount);
	}
}