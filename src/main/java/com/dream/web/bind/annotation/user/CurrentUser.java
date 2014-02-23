package com.dream.web.bind.annotation.user;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dream.common.Constants;

/**
 * 绑定当前登录的用户,不同于@ModelAttribute 
 * User: xiaorui.lu 
 * Date: 2013年12月17日 下午1:39:35
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {

  /**
   * 当前用户在request中的名字 默认{@link Constants#CURRENT_USER}
   * 
   * @return
   */
  String value() default Constants.CURRENT_USER;

}
