package com.korres.service.impl;

import java.util.List;
import javax.annotation.Resource;
import com.korres.dao.AreaDao;
import com.korres.entity.Area;
import com.korres.service.AreaService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("areaServiceImpl")
public class AreaServiceImpl extends BaseServiceImpl<Area, Long> implements
		AreaService {

	@Resource(name = "areaDaoImpl")
	private AreaDao areaDao;

	@Resource(name = "areaDaoImpl")
	public void setBaseDao(AreaDao areaDao) {
		super.setBaseDao(areaDao);
	}

	@Transactional(readOnly = true)
	public List<Area> findRoots() {
		return this.areaDao.findRoots(null);
	}

	@Transactional(readOnly = true)
	public List<Area> findRoots(Integer count) {
		return this.areaDao.findRoots(count);
	}

	@Transactional
	@CacheEvict(value = { "area" }, allEntries = true)
	public void save(Area area) {
		super.save(area);
	}

	@Transactional
	@CacheEvict(value = { "area" }, allEntries = true)
	public Area update(Area area) {
		return super.update(area);
	}

	@Transactional
	@CacheEvict(value = { "area" }, allEntries = true)
	public Area update(Area area, String[] ignoreProperties) {
		return super.update(area, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "area" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "area" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "area" }, allEntries = true)
	public void delete(Area area) {
		super.delete(area);
	}
}