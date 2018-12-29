package com.korres.service;

import java.util.List;

import com.korres.Filter;
import com.korres.Order;

import com.korres.entity.FriendLink;
import com.korres.entity.FriendLink.FriendLinkType;

public abstract interface FriendLinkService extends
		BaseService<FriendLink, Long> {
	public abstract List<FriendLink> findList(FriendLinkType paramType);

	public abstract List<FriendLink> findList(Integer paramInteger,
			List<Filter> paramList, List<Order> paramList1, String paramString);
}