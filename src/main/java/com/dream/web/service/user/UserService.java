package com.dream.web.service.user;

import java.util.Date;
import java.util.List;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.dream.common.entity.User;
import com.dream.common.exception.user.UserNotExistsException;
import com.dream.common.exception.user.UserPasswordNotMatchException;
import com.dream.common.inject.annotation.BaseComponent;
import com.dream.common.service.BaseService;
import com.dream.common.utils.user.UserLogUtils;
import com.dream.web.repository.user.UserRepository;
import com.google.common.collect.Lists;

/**
 * User: xiaorui.lu Date: 2013年12月13日 下午5:14:20
 */
@Service
public class UserService extends BaseService<User, Long> {

	@Autowired
	@BaseComponent
	public UserRepository userRepository;

	@Autowired
	private PasswordService passwordService;

	public List<User> findAll() {
		return Lists.newArrayList(userRepository.findAll());
	}

	public User findOne(long id) {
		return userRepository.findOne(id);
	}

	public User save(User user) {
		if (user.getCreatetime() == null) {
			user.setCreatetime(new Date());
		}
		user.randomSalt();
		user.setPassword(passwordService.encryptPassword(user.getName(), user.getPassword(), user.getSalt()));

		return super.save(user);
	}

	public User findByName(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return userRepository.findByName(name);
	}

	public User login(String name, String password) {

		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(password)) {
			UserLogUtils.log(name, "loginError", "username is empty");
			throw new UserNotExistsException();
		}
		// 密码如果不在指定范围内 肯定错误
		if (password.length() < User.PASSWORD_MIN_LENGTH || password.length() > User.PASSWORD_MAX_LENGTH) {
			UserLogUtils.log(name, "loginError", "password length error! password is between {} and {}", User.PASSWORD_MIN_LENGTH,
					User.PASSWORD_MAX_LENGTH);

			throw new UserPasswordNotMatchException();
		}

		User user = null;

		// 此处需要走代理对象，目的是能走缓存切面
		UserService proxyUserService = (UserService) AopContext.currentProxy();
		if (maybeUsername(name)) {
			user = proxyUserService.findByName(name);
		}

		if (user == null) {
			UserLogUtils.log(name, "loginError", "user is not exists!");
			throw new UserNotExistsException();
		}

		passwordService.validate(user, password);

		UserLogUtils.log(name, "loginSuccess", "");
		return user;
	}

	public User loginByUdid(String udid) {
		if (StringUtils.isEmpty(udid)) {
			return null;
		}
		return userRepository.findByUdid(udid);
	}

	private boolean maybeUsername(String username) {
		if (!username.matches(User.NAME_PATTERN)) {
			return false;
		}
		// 如果用户名不在指定范围内也是错误的
		if (username.length() < User.USERNAME_MIN_LENGTH || username.length() > User.USERNAME_MAX_LENGTH) {
			return false;
		}

		return true;
	}
}
