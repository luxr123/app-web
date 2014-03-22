package com.dream.web.service.user;


import net.sf.ehcache.Element;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dream.common.entity.User;
import com.dream.common.exception.user.UserPasswordNotMatchException;
import com.dream.common.exception.user.UserPasswordRetryLimitExceedException;
import com.dream.common.utils.user.UserLogUtils;
import com.dream.common.web.utils.Md5Utils;
import com.dream.web.cache.factory.EcacheFactory;
import com.dream.web.service.user.Config;

/**
 * User: xiaorui.lu 
 * Date: 2013年12月15日 下午9:07:02
 */
@Service
public class PasswordService {

	@Value(value = "${user.password.maxRetryCount}")
	private int maxRetryCount = 10;

	public void setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

	public void validate(User user, String password) {
		String name = user.getName() + "_retry";

		int retryCount = 0;

		Element cacheElement = (Element) EcacheFactory.getCacheInstance().getElement(Config.USER_CACHE, name);
		if (cacheElement != null) {
			retryCount = (Integer) cacheElement.getObjectValue();
			if (retryCount >= maxRetryCount) {
				UserLogUtils.log(name, "passwordError", "password error, retry limit exceed! password: {},max retry count {}",
						password, maxRetryCount);
				throw new UserPasswordRetryLimitExceedException(maxRetryCount);
			}
		}

		if (!matches(user, password)) {
		    EcacheFactory.getCacheInstance().put(Config.USER_CACHE,name, ++retryCount);
			UserLogUtils.log(name, "passwordError", "password error! password: {} retry count: {}", password, retryCount);
			throw new UserPasswordNotMatchException();
		} else {
			clearLoginRecordCache(name);
		}
	}

	public boolean matches(User user, String newPassword) {
		return user.getPassword().equals(encryptPassword(user.getName(), newPassword, user.getSalt()));
	}

	public void clearLoginRecordCache(String username) {
	  EcacheFactory.getCacheInstance().remvoeElement(Config.USER_CACHE,username);
	}

	public String encryptPassword(String username, String password, String salt) {
		return Md5Utils.hash(username + password + salt);
	}
	
}
