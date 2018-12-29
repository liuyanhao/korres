package com.korres.service.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import com.korres.dao.GoodsDao;
import com.korres.dao.ProductDao;
import com.korres.entity.Goods;
import com.korres.entity.Product;
import com.korres.service.GoodsService;
import com.korres.service.StaticService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service("goodsServiceImpl")
public class GoodsServiceImpl extends BaseServiceImpl<Goods, Long>
  implements GoodsService
{

  @Resource(name="goodsDaoImpl")
  private GoodsDao goodsDao;

  @Resource(name="productDaoImpl")
  private ProductDao productDao;

  @Resource(name="staticServiceImpl")
  private StaticService staticService;

  @Resource(name="goodsDaoImpl")
  public void setBaseDao(GoodsDao goodsDao)
  {
    super.setBaseDao(goodsDao);
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void save(Goods goods)
  {
    Assert.notNull(goods);
    super.save(goods);
    this.goodsDao.flush();
    if (goods.getProducts() != null)
    {
      Iterator localIterator = goods.getProducts().iterator();
      while (localIterator.hasNext())
      {
        Product localProduct = (Product)localIterator.next();
        this.staticService.build(localProduct);
      }
    }
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public Goods update(Goods goods)
  {
    Assert.notNull(goods);
    HashSet localHashSet = new HashSet();
//    CollectionUtils.select(goods.getProducts(), new GoodsServiceImpl$1(this), localHashSet);
    
    CollectionUtils.select(goods.getProducts(), new Predicate(){
		public boolean evaluate(Object arg0) {
			Product localProduct = (Product)arg0;
		    return (localProduct != null) && (localProduct.getId() != null);
		}
    }, localHashSet);
    
    
    List<Product> localList = this.productDao.findList(goods, localHashSet);
    Iterator iterator = localList.iterator();
    while (iterator.hasNext())
    {
    	Product product = (Product)iterator.next();
      this.staticService.delete(product);
    }
    Goods gs = (Goods)super.update(goods);
    this.goodsDao.flush();
    if (gs.getProducts() != null)
    {
      Iterator localIterator = gs.getProducts().iterator();
      while (localIterator.hasNext())
      {
    	  Product product = (Product)localIterator.next();
        this.staticService.build(product);
      }
    }
    return gs;
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public Goods update(Goods goods, String[] ignoreProperties)
  {
    return (Goods)super.update(goods, ignoreProperties);
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void delete(Long id)
  {
    super.delete(id);
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void delete(Long[] ids)
  {
    super.delete(ids);
  }

  @Transactional
  @CacheEvict(value={"product", "productCategory", "review", "consultation"}, allEntries=true)
  public void delete(Goods goods)
  {
    if ((goods != null) && (goods.getProducts() != null))
    {
      Iterator localIterator = goods.getProducts().iterator();
      while (localIterator.hasNext())
      {
        Product localProduct = (Product)localIterator.next();
        this.staticService.delete(localProduct);
      }
    }
    super.delete(goods);
  }
}