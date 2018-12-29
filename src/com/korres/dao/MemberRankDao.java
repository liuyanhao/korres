package com.korres.dao;

import java.math.BigDecimal;
import com.korres.entity.MemberRank;

public abstract interface MemberRankDao extends BaseDao<MemberRank, Long> {
	public abstract boolean nameExists(String paramString);

	public abstract boolean amountExists(BigDecimal paramBigDecimal);

	public abstract MemberRank findDefault();

	public abstract MemberRank findByAmount(BigDecimal paramBigDecimal);
}