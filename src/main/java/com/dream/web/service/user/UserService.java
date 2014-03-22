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
import com.dream.web.cache.factory.EcacheFactory;
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
    User user = (User)EcacheFactory.getCacheInstance().getElement(Config.USER_CACHE, id);
    if(user != null){
      return user;
    }
    user= userRepository.findOne(id);
    EcacheFactory.getCacheInstance().put(Config.USER_CACHE, id, user);
    return user;
  }

  public User save(User user) {
    if (user.getCreatetime() == null) {
      user.setCreatetime(new Date());
    }
    user.randomSalt();
    user.setPassword(passwordService.encryptPassword(user.getName(), user.getPassword(),
        user.getSalt()));
    User u = super.save(user);
    EcacheFactory.getCacheInstance().put(Config.USER_CACHE, u.getId(), u);
    return u;
  }

  public User update(User user) {
    User u = super.update(user);
    if (u != null) {
      long id = u.getId();
      EcacheFactory.getCacheInstance().getCache(Config.USER_CACHE).acquireWriteLockOnKey(id);

      EcacheFactory.getCacheInstance().put(Config.USER_CACHE, id, u);

      EcacheFactory.getCacheInstance().getCache(Config.USER_CACHE).releaseWriteLockOnKey(id);
    }
    return u;
  }

  public User findByName(String name) {
    if (StringUtils.isEmpty(name)) {
      return null;
    }
    User u = userRepository.findByName(name);
    if (u != null) {
      EcacheFactory.getCacheInstance().put(Config.USER_CACHE, u.getName(), u);
    }
    return u;
  }

  public User login(String name, String password) {

    if (StringUtils.isEmpty(name) || StringUtils.isEmpty(password)) {
      UserLogUtils.log(name, "loginError", "username is empty");
      throw new UserNotExistsException();
    }
    // 密码如果不在指定范围内 肯定错误
    if (password.length() < User.PASSWORD_MIN_LENGTH
        || password.length() > User.PASSWORD_MAX_LENGTH) {
      UserLogUtils.log(name, "loginError", "password length error! password is between {} and {}",
          User.PASSWORD_MIN_LENGTH, User.PASSWORD_MAX_LENGTH);

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
      return null;
      // throw new UserNotExistsException();
    }

    passwordService.validate(user, password);
    EcacheFactory.getCacheInstance().put(Config.USER_CACHE, user.getId(), user);
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
    if (username.length() < User.USERNAME_MIN_LENGTH
        || username.length() > User.USERNAME_MAX_LENGTH) {
      return false;
    }

    return true;
  }
}
