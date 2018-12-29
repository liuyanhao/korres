package com.korres.dao;

import java.util.List;

import com.korres.entity.FriendLink;
import com.korres.entity.FriendLink.FriendLinkType;

public abstract interface FriendLinkDao extends BaseDao<FriendLink, Long> {
	public abstract List<FriendLink> findList(FriendLinkType paramType);
}