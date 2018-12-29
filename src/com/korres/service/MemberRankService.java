package com.korres.service;

import java.math.BigDecimal;
import com.korres.entity.MemberRank;

public abstract interface MemberRankService extends
		BaseService<MemberRank, Long> {
	public abstract boolean nameExists(String paramString);

	public abstract boolean nameUnique(String paramString1, String paramString2);

	public abstract boolean amountExists(BigDecimal paramBigDecimal);

	public abstract boolean amountUnique(BigDecimal paramBigDecimal1,
			BigDecimal paramBigDecimal2);

	public abstract MemberRank findDefault();

	public abstract MemberRank findByAmount(BigDecimal paramBigDecimal);
}