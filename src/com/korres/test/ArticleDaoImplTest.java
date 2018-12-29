package com.korres.test;

import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.Resource;

import com.korres.dao.ArticleCategoryDao;
import com.korres.dao.ArticleDao;
import com.korres.dao.TagDao;
import com.korres.entity.Article;
import com.korres.entity.ArticleCategory;
import com.korres.entity.Tag;
import com.korres.entity.Tag.TagType;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/applicationContext.xml",
		"classpath*:/applicationContext-mvc.xml" })
@Transactional
public class ArticleDaoImplTest {

	@Resource(name = "articleCategoryDaoImpl")
	private ArticleCategoryDao IIIllIlI;

	@Resource(name = "tagDaoImpl")
	private TagDao IIIllIll;

	@Resource(name = "articleDaoImpl")
	private ArticleDao IIIlllII;
	private static final Logger IIIlllIl = LoggerFactory
			.getLogger(ArticleDaoImplTest.class);
	private static Long[] IIIllllI = new Long[100];
	private static Long[] IIIlllll = new Long[100];
	private static Long[] IIlIIIII = new Long[20];

	@Before
	public void prepareTestData() {
		String str;
		for (int i = 0; i < IIIllllI.length; i++) {
			str = "test" + i;
			ArticleCategory localObject = new ArticleCategory();
			if (i < 20) {
				localObject.setName(str);
				localObject.setOrder(Integer.valueOf(i));
				this.IIIllIlI.persist(localObject);
			} else {
				localObject.setName(str);
				localObject.setOrder(Integer.valueOf(i));
				localObject.setParent((ArticleCategory) this.IIIllIlI
						.find(IIIllllI[0]));
				this.IIIllIlI.persist(localObject);
			}
			IIIllllI[i] = ((ArticleCategory) localObject).getId();
		}
		this.IIIllIlI.flush();
		this.IIIllIlI.clear();

		for (int i = 0; i < IIlIIIII.length; i++) {
			str = "test" + i;
			Tag localObject = new Tag();
			((Tag) localObject).setName(str);
			((Tag) localObject).setOrder(Integer.valueOf(i));
			((Tag) localObject).setType(TagType.article);
			this.IIIllIll.persist(localObject);
			IIlIIIII[i] = ((Tag) localObject).getId();
		}
		this.IIIllIll.flush();
		this.IIIllIll.clear();
		for (int i = 0; i < IIIlllll.length; i++) {
			str = "test" + i;
			Article localObject = new Article();
			((Article) localObject).setTitle(str);
			((Article) localObject).setContent(str);
			((Article) localObject).setIsPublication(Boolean.valueOf(true));
			((Article) localObject).setIsTop(Boolean.valueOf(false));
			((Article) localObject).setHits(Long.valueOf(0L));
			if (i < 20)
				((Article) localObject)
						.setArticleCategory((ArticleCategory) this.IIIllIlI
								.find(IIIllllI[0]));
			else
				((Article) localObject)
						.setArticleCategory((ArticleCategory) this.IIIllIlI
								.find(IIIllllI[1]));
			if (i < 20) {
				HashSet localHashSet = new HashSet();
				if (i < 10) {
					localHashSet.add((Tag) this.IIIllIll.find(IIlIIIII[0]));
					localHashSet.add((Tag) this.IIIllIll.find(IIlIIIII[1]));
				}
				localHashSet.add((Tag) this.IIIllIll.find(IIlIIIII[2]));
				((Article) localObject).setTags(localHashSet);
			}
			this.IIIlllII.persist(localObject);
			IIIlllll[i] = ((Article) localObject).getId();
		}
		this.IIIlllII.flush();
		this.IIIlllII.clear();
		IIIlllIl.info("prepare test data");
	}

	@Test
	public void testFindList() {
		ArrayList localArrayList = new ArrayList();
		localArrayList.add((Tag) this.IIIllIll.find(IIlIIIII[0]));
		localArrayList.add((Tag) this.IIIllIll.find(IIlIIIII[2]));
		MatcherAssert.assertThat(Integer.valueOf(this.IIIlllII.findList(null,
				localArrayList, null, null, null).size()), Matchers.is(Integer
				.valueOf(80)));
	}
}