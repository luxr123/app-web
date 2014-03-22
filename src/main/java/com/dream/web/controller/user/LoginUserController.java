//package com.dream.web.controller.user;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
//import org.apache.commons.lang3.builder.ToStringStyle;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authc.AuthenticationException;
//import org.apache.shiro.authc.ExcessiveAttemptsException;
//import org.apache.shiro.authc.IncorrectCredentialsException;
//import org.apache.shiro.authc.LockedAccountException;
//import org.apache.shiro.authc.UnknownAccountException;
//import org.apache.shiro.authc.UsernamePasswordToken;
//import org.apache.shiro.subject.Subject;
//import org.apache.shiro.web.util.WebUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.springframework.web.servlet.view.InternalResourceViewResolver;
//
//import com.dream.common.Constants;
//import com.dream.common.controller.BaseController;
//import com.dream.common.entity.User;
//import com.dream.common.entity.user.UserStatus;
//import com.dream.common.enums.BooleanEnum;
//import com.dream.common.inject.annotation.BaseComponent;
//import com.dream.web.bind.annotation.user.CurrentUser;
//import com.dream.web.service.user.PasswordService;
//import com.dream.web.service.user.UserService;
//
///**
// * 登录用户的个人信息
// * User: xiaorui.lu 
// * Date: 2013年12月17日 下午1:34:39
// *
// */
//@Controller
//@RequestMapping("/user/loginUser")
//public class LoginUserController extends BaseController<User, Long> {
//
//  @Autowired
//  @BaseComponent
//  private UserService userService;
//
//  @Autowired
//  private PasswordService passwordService;
//
//  public void setCommonData(Model model) {
//    model.addAttribute("booleanList", BooleanEnum.values());
//    model.addAttribute("statusList", UserStatus.values());
//  }
//
//  /**
//   * 获取验证码图片和文本(验证码文本会保存在HttpSession中) 用户登录
//   */
//  @RequestMapping(value = "/login", method = RequestMethod.POST)
//  public String login(HttpServletRequest request) {
//    String resultPageURL = InternalResourceViewResolver.FORWARD_URL_PREFIX + "/";
//    String username = request.getParameter("username");
//    String password = request.getParameter("password");
//    // 获取HttpSession中的验证码
//    String verifyCode = (String) request.getSession().getAttribute(
//        com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
//    // 获取用户请求表单中输入的验证码
//    String submitCode = WebUtils.getCleanParam(request, "verifyCode");
//    System.out.println("用户[" + username + "]登录时输入的验证码为[" + submitCode + "],HttpSession中的验证码为["
//        + verifyCode + "]");
//    if (StringUtils.isEmpty(submitCode)
//        || !StringUtils.equals(verifyCode.toLowerCase(), submitCode.toLowerCase())) {
//      request.setAttribute("message_login", "验证码不正确");
//      return resultPageURL;
//    }
//    UsernamePasswordToken token = new UsernamePasswordToken(username, password);
//    token.setRememberMe(true);
//    System.out.println("为了验证登录用户而封装的token为"
//        + ReflectionToStringBuilder.toString(token, ToStringStyle.MULTI_LINE_STYLE));
//    // 获取当前的Subject
//    Subject currentUser = SecurityUtils.getSubject();
//    try {
//      // 在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
//      // 每个Realm都能在必要时对提交的AuthenticationTokens作出反应
//      // 所以这一步在调用login(token)方法时,它会走到UserRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
//      System.out.println("对用户[" + username + "]进行登录验证..验证开始");
//      currentUser.login(token);
//      System.out.println("对用户[" + username + "]进行登录验证..验证通过");
//      resultPageURL = "main";
//    } catch (UnknownAccountException uae) {
//      System.out.println("对用户[" + username + "]进行登录验证..验证未通过,未知账户");
//      request.setAttribute("message_login", "未知账户");
//    } catch (IncorrectCredentialsException ice) {
//      System.out.println("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");
//      request.setAttribute("message_login", "密码不正确");
//    } catch (LockedAccountException lae) {
//      System.out.println("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");
//      request.setAttribute("message_login", "账户已锁定");
//    } catch (ExcessiveAttemptsException eae) {
//      System.out.println("对用户[" + username + "]进行登录验证..验证未通过,错误次数过多");
//      request.setAttribute("message_login", "用户名或密码错误次数过多");
//    } catch (AuthenticationException ae) {
//      // 通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
//      System.out.println("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");
//      ae.printStackTrace();
//      request.setAttribute("message_login", "用户名或密码不正确");
//    }
//    // 验证是否登录成功
//    if (currentUser.isAuthenticated()) {
//      System.out.println("用户[" + username + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
//    } else {
//      token.clear();
//    }
//    return resultPageURL;
//  }
//
//  /**
//   * 用户登出
//   */
//  @RequestMapping("/logout")
//  public String logout(HttpServletRequest request) {
//    SecurityUtils.getSubject().logout();
//    return InternalResourceViewResolver.REDIRECT_URL_PREFIX + "/";
//  }
//  
//  
//  /**
//   * 下面是处理普通用户访问的UserController.java
//   */
//  @RequestMapping(value = "/getUserInfo")
//  public String getUserInfo(HttpServletRequest request) {
//    String currentUser = (String) request.getSession().getAttribute("currentUser");
//    System.out.println("当前登录的用户为[" + currentUser + "]");
//    request.setAttribute("currUser", currentUser);
//    return "/user/info";
//  }
//  
//
//  @RequestMapping(value = "/updateInfo", method = RequestMethod.GET)
//  public String updateInfoForm(@CurrentUser
//  User user, Model model) {
//    setCommonData(model);
//    model.addAttribute(Constants.OP_NAME, "修改个人资料");
//    model.addAttribute("user", user);
//    return viewName("editForm");
//  }
//
//  @RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
//  public String updateInfo(@CurrentUser
//  User user, @RequestParam("email")
//  String email, @RequestParam("mobilePhoneNumber")
//  String mobilePhoneNumber, Model model, RedirectAttributes redirectAttributes) {
//
//    if (email == null || !email.matches(User.EMAIL_PATTERN)) {
//      model.addAttribute(Constants.ERROR, "请输入正确的邮箱地址");
//      return updateInfoForm(user, model);
//    }
//
//    if (mobilePhoneNumber == null || !mobilePhoneNumber.matches(User.MOBILE_PHONE_NUMBER_PATTERN)) {
//      model.addAttribute(Constants.ERROR, "请输入正确的手机号");
//      return updateInfoForm(user, model);
//    }
//
//    User emailDbUser = userService.findByEmail(email);
//    if (emailDbUser != null && !emailDbUser.equals(user)) {
//      model.addAttribute(Constants.ERROR, "邮箱地址已经被其他人使用，请换一个");
//      return updateInfoForm(user, model);
//    }
//
//    User mobilePhoneNumberDbUser = userService.findByMobilePhoneNumber(mobilePhoneNumber);
//    if (mobilePhoneNumberDbUser != null && !mobilePhoneNumberDbUser.equals(user)) {
//      model.addAttribute(Constants.ERROR, "手机号已经被其他人使用，请换一个");
//      return updateInfoForm(user, model);
//    }
//
//    user.setEmail(email);
//    user.setMobilePhoneNumber(mobilePhoneNumber);
//    userService.update(user);
//
//    redirectAttributes.addFlashAttribute(Constants.MESSAGE, "修改个人资料成功");
//
//    return redirectToUrl(viewName("updateInfo"));
//
//  }
//
//  @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
//  public String changePasswordForm(@CurrentUser
//  User user, Model model) {
//    setCommonData(model);
//    model.addAttribute(Constants.OP_NAME, "修改密码");
//    model.addAttribute("user", user);
//    return viewName("changePasswordForm");
//  }
//
//  @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
//  public String changePassword(@CurrentUser
//  User user, @RequestParam(value = "oldPassword")
//  String oldPassword, @RequestParam(value = "newPassword1")
//  String newPassword1, @RequestParam(value = "newPassword2")
//  String newPassword2, Model model, RedirectAttributes redirectAttributes) {
//
//    if (!passwordService.matches(user, oldPassword)) {
//      model.addAttribute(Constants.ERROR, "旧密码不正确");
//      return changePasswordForm(user, model);
//    }
//
//    if (StringUtils.isEmpty(newPassword1) || StringUtils.isEmpty(newPassword2)) {
//      model.addAttribute(Constants.ERROR, "必须输入新密码");
//      return changePasswordForm(user, model);
//    }
//
//    if (!newPassword1.equals(newPassword2)) {
//      model.addAttribute(Constants.ERROR, "两次输入的密码不一致");
//      return changePasswordForm(user, model);
//    }
//
//    userService.changePassword(user, newPassword1);
//
//    redirectAttributes.addFlashAttribute(Constants.MESSAGE, "修改密码成功");
//    return redirectToUrl(viewName("changePassword"));
//  }
//
//}
