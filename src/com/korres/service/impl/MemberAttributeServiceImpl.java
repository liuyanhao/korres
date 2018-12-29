package com.korres.service.impl;

import java.util.List;
import javax.annotation.Resource;
import com.korres.dao.MemberAttributeDao;
import com.korres.entity.MemberAttribute;
import com.korres.service.MemberAttributeService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("memberAttributeServiceImpl")
public class MemberAttributeServiceImpl extends
		BaseServiceImpl<MemberAttribute, Long> implements
		MemberAttributeService {

	@Resource(name = "memberAttributeDaoImpl")
	private MemberAttributeDao memberAttributeDao;

	@Resource(name = "memberAttributeDaoImpl")
	public void setBaseDao(MemberAttributeDao memberAttributeDao) {
		super.setBaseDao(memberAttributeDao);
	}

	@Transactional(readOnly = true)
	public Integer findUnusedPropertyIndex() {
		return this.memberAttributeDao.findUnusedPropertyIndex();
	}

	@Transactional(readOnly = true)
	public List<MemberAttribute> findList() {
		return this.memberAttributeDao.findList();
	}

	@Transactional(readOnly = true)
	@Cacheable( { "memberAttribute" })
	public List<MemberAttribute> findList(String cacheRegion) {
		return this.memberAttributeDao.findList();
	}

	@Transactional
	@CacheEvict(value = { "memberAttribute" }, allEntries = true)
	public void save(MemberAttribute memberAttribute) {
		super.save(memberAttribute);
	}

	@Transactional
	@CacheEvict(value = { "memberAttribute" }, allEntries = true)
	public MemberAttribute update(MemberAttribute memberAttribute) {
		return super.update(memberAttribute);
	}

	@Transactional
	@CacheEvict(value = { "memberAttribute" }, allEntries = true)
	public MemberAttribute update(MemberAttribute memberAttribute,
			String[] ignoreProperties) {
		return super.update(memberAttribute, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "memberAttribute" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "memberAttribute" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "memberAttribute" }, allEntries = true)
	public void delete(MemberAttribute memberAttribute) {
		super.delete(memberAttribute);
	}
}