package com.dream.web.service.user;

import javax.annotation.PostConstruct;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dream.common.entity.User;
import com.dream.common.exception.user.UserPasswordNotMatchException;
import com.dream.common.exception.user.UserPasswordRetryLimitExceedException;
import com.dream.common.utils.user.UserLogUtils;
import com.dream.common.web.utils.Md5Utils;

/**
 * User: xiaorui.lu 
 * Date: 2013年12月15日 下午9:07:02
 */
@Service
public class PasswordService {

	@Autowired
	private CacheManager ehcacheManager;

	private Cache loginRecordCache;

	@Value(value = "${user.password.maxRetryCount}")
	private int maxRetryCount = 10;

	public void setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

	@PostConstruct
	public void init() {
		loginRecordCache = ehcacheManager.getCache("loginRecordCache");
	}

	public void validate(User user, String password) {
		String name = user.getName();

		int retryCount = 0;

		Element cacheElement = loginRecordCache.get(name); ///  ????????? 有问题
		if (cacheElement != null) {
			retryCount = (Integer) cacheElement.getObjectValue();
			if (retryCount >= maxRetryCount) {
				UserLogUtils.log(name, "passwordError", "password error, retry limit exceed! password: {},max retry count {}",
						password, maxRetryCount);
				throw new UserPasswordRetryLimitExceedException(maxRetryCount);
			}
		}

		if (!matches(user, password)) {
			loginRecordCache.put(new Element(name, ++retryCount));
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
		loginRecordCache.remove(username);
	}

	public String encryptPassword(String username, String password, String salt) {
		return Md5Utils.hash(username + password + salt);
	}
}
