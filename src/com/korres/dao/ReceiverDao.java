package com.korres.dao;

import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Member;
import com.korres.entity.Receiver;

public abstract interface ReceiverDao extends BaseDao<Receiver, Long> {
	public abstract Receiver findDefault(Member paramMember);

	public abstract Page<Receiver> findPage(Member paramMember,
			Pageable paramPageable);
}