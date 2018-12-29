package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.ReceiverDao;
import com.korres.entity.Member;
import com.korres.entity.Receiver;
import com.korres.service.ReceiverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Page;
import com.korres.Pageable;

@Service("receiverServiceImpl")
public class ReceiverServiceImpl extends BaseServiceImpl<Receiver, Long>
		implements ReceiverService {

	@Resource(name = "receiverDaoImpl")
	private ReceiverDao receiverDao;

	@Resource(name = "receiverDaoImpl")
	public void setBaseDao(ReceiverDao receiverDao) {
		super.setBaseDao(receiverDao);
	}

	@Transactional(readOnly = true)
	public Receiver findDefault(Member member) {
		return this.receiverDao.findDefault(member);
	}

	@Transactional(readOnly = true)
	public Page<Receiver> findPage(Member member, Pageable pageable) {
		return this.receiverDao.findPage(member, pageable);
	}
}