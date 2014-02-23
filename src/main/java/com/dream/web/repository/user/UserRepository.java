package com.dream.web.repository.user;

import com.dream.common.entity.User;
import com.dream.common.repository.BaseRepository;

/**
 * User: xiaorui.lu 
 * Date: 2013年12月13日 下午5:01:30
 */
public interface UserRepository extends BaseRepository<User, Long> {

	User findByName(String name);
	
	User findByUdid(String udid);
}
