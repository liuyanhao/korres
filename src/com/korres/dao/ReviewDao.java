package com.korres.dao;

import java.util.List;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.entity.Review;
import com.korres.entity.Review.ReviewType;

public abstract interface ReviewDao extends BaseDao<Review, Long> {
	public abstract List<Review> findList(Member paramMember,
			Product paramProduct, ReviewType paramType, Boolean paramBoolean,
			Integer paramInteger, List<Filter> paramList, List<Order> paramList1);

	public abstract Page<Review> findPage(Member paramMember,
			Product paramProduct, ReviewType paramType, Boolean paramBoolean,
			Pageable paramPageable);

	public abstract Long count(Member paramMember, Product paramProduct,
			ReviewType paramType, Boolean paramBoolean);

	public abstract boolean isReviewed(Member paramMember, Product paramProduct);

	public abstract long calculateTotalScore(Product paramProduct);

	public abstract long calculateScoreCount(Product paramProduct);
}