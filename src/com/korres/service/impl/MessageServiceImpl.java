package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.MessageDao;
import com.korres.entity.Member;
import com.korres.entity.Message;
import com.korres.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Page;
import com.korres.Pageable;

@Service("messageServiceImpl")
public class MessageServiceImpl extends BaseServiceImpl<Message, Long>
		implements MessageService {

	@Resource(name = "messageDaoImpl")
	private MessageDao messageDao;

	@Resource(name = "messageDaoImpl")
	public void setBaseDao(MessageDao messageDao) {
		super.setBaseDao(messageDao);
	}

	@Transactional(readOnly = true)
	public Page<Message> findPage(Member member, Pageable pageable) {
		return this.messageDao.findPage(member, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Message> findDraftPage(Member sender, Pageable pageable) {
		return this.messageDao.findDraftPage(sender, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(Member member, Boolean read) {
		return this.messageDao.count(member, read);
	}

	public void delete(Long id, Member member) {
		this.messageDao.remove(id, member);
	}
}