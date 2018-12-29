package com.korres.service;

import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Member;
import com.korres.entity.Message;

public abstract interface MessageService extends BaseService<Message, Long> {
	public abstract Page<Message> findPage(Member paramMember,
			Pageable paramPageable);

	public abstract Page<Message> findDraftPage(Member paramMember,
			Pageable paramPageable);

	public abstract Long count(Member paramMember, Boolean paramBoolean);

	public abstract void delete(Long paramLong, Member paramMember);
}