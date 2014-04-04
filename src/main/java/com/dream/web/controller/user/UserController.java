package com.dream.web.controller.user;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.dream.common.Constants;
import com.dream.common.constants.ErrorCode;
import com.dream.common.controller.BaseCRUDController;
import com.dream.common.entity.User;
import com.dream.common.inject.annotation.BaseComponent;
import com.dream.web.service.jpush.UserJpush;
import com.dream.web.service.user.Config;
import com.dream.web.service.user.PasswordService;
import com.dream.web.service.user.UserService;
import com.dream.web.util.TaskUtil;
import com.google.code.kaptcha.Producer;


/**
 * User: xiaorui.lu 
 * Date: 2013年12月13日 下午5:31:56
 */

@Controller
@RequestMapping("/user")
public class UserController extends BaseCRUDController<User, Long> {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Producer captchaProducer = null;

	@Autowired
	public void setCaptchaProducer(Producer captchaProducer) {
		this.captchaProducer = captchaProducer;
	}

	@Autowired
	@BaseComponent
	private UserService userService;
	
	@Autowired
	private PasswordService passwordService;

	/**
	 * 注册用户
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody User register(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String guid = UUID.randomUUID().toString();
		response.setDateHeader("Expires", 0);
	    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
	    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
	    response.setHeader("Pragma", "no-cache");
	    String capText = captchaProducer.createText();
	    request.getSession().setAttribute(Constants.GUID, guid);
	    request.getSession().setAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY, capText);
	    logger.debug("register Get请求处理完毕");
	    User user = new User();
	    user.setCheckcode(capText);
	    user.setGuid(guid);
		return user;
	}

	
	   /**
     * 注册添加用户
     * 
     * @param user
     * @param session
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
    public @ResponseBody ErrorCode addUser(@RequestParam("userJson") String userJson, @RequestParam("file") MultipartFile file) {// CommonsMultipartFile   
      User user = JSONObject.parseObject(userJson, User.class);
      user.setId(0l);
       if (!file.isEmpty()) {
          String iconPath = TaskUtil.saveImgFile(file, Config.USER_ICON_DIR);  //保存原图
          String compressPath = TaskUtil.compressPic(iconPath, Config.COMPRESS_USER_ICON_WIDTH, Config.COMPRESS_USER_ICON_HEIGHT); //保存缩略图
          user.setRole(false);
          user.setIconPath(compressPath);
          User u = userService.save(user);
          return new ErrorCode(ErrorCode.CODE_SUCCESS, u.getName() + "," + String.valueOf(u.getId()));                  
        } else {
            return new ErrorCode(ErrorCode.CODE_FILE_NOT_EXIST, "file upload failed!");
        }
    }
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody ErrorCode login(@RequestParam("name") String name, @RequestParam("password") String password,@RequestParam("udid") String udid){
		System.out.println("enter login name:" + name + "-- password:" + password);
		User user = userService.login(name, password);
		if( user != null && !(user.getUdid().equals(udid)) ){
		  if(!udid.equals(user.getUdid())){		    
		    userService.setUdidEmpity(udid);
		    user.setUdid(udid);
	        userService.update(user);
		  }
		  return new ErrorCode(ErrorCode.CODE_SUCCESS, user.getName() + "," + user.getId());
		}
		 return ErrorCode.NOT_EXIT;
	}
	
	   @RequestMapping(value = "/udidLogin", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	    public @ResponseBody ErrorCode login(@RequestParam("udid") String udid){
	       User user = userService.loginByUdid(udid);
	       if(user != null){
	         //UserJpush.loginJpush(udid, user);
	         return new ErrorCode(ErrorCode.CODE_SUCCESS, user.getName() + "," + user.getId());
	       }
	       return ErrorCode.NOT_EXIT;
	    }
	   
	   @RequestMapping(value = "/loginJpush", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
       public @ResponseBody void jpush(@RequestParam("udid") String udid){
          User user = userService.loginByUdid(udid);
          if(user != null){
            UserJpush.loginJpush(udid, user);
          }
       }
	   /**
	    * 获得压缩图
	    * @param id
	    * @return
	    */
	  /*  @RequestMapping(value = "/getCompressIcon", method = RequestMethod.POST)
	    public @ResponseBody String getCompressIcon(@RequestParam("id") String id) {
	      User user = userService.findOne(Long.parseLong(id));
	      String compressIcon = user.getIconPath();
	      String fileStream = null;
	        try {
	          fileStream = TaskUtil.iconToByte(compressIcon);
	        } catch (IOException e) {
	          e.printStackTrace();
	        }
	       return fileStream;
	  }*/
	   
	   @RequestMapping(value = "/getCompressIcon", method = RequestMethod.POST)
       public @ResponseBody String getCompressIcon(@RequestParam("url") String compressPath) {
         String fileStream = null;
           try {
             fileStream = TaskUtil.iconToByte(compressPath);
           } catch (IOException e) {
             e.printStackTrace();
           }
          return fileStream;
     }
	    
	    
	    
	    /**
	     * 获得原图
	     * @param id
	     * @return
	     */
	    /*@RequestMapping(value = "/getOriginalIcon", method = RequestMethod.POST)
        public @ResponseBody String getOriginalIcon(@RequestParam("id") String id) {
          User user = userService.findOne(Long.parseLong(id));
          String originalIcon = TaskUtil.convertPath(user.getIconPath(), Config.ORGINAL_ICON_DIR);
          String fileStream = null;
          try {
            fileStream = TaskUtil.iconToByte(originalIcon);
          } catch (IOException e) {
            e.printStackTrace();
          }
         return fileStream;// 返回Base64编码过的字节数组字符串
      }*/
	   
	   
	   @RequestMapping(value = "/getOriginalIcon", method = RequestMethod.POST)
       public @ResponseBody String getOriginalIcon(@RequestParam("url") String compressPath) {
         String originalPath = TaskUtil.convertPath(compressPath, Config.ORGINAL_ICON_DIR);
         String fileStream = null;
         try {
           fileStream = TaskUtil.iconToByte(originalPath);
         } catch (IOException e) {
           e.printStackTrace();
         }
        return fileStream;// 返回Base64编码过的字节数组字符串
     }
	  
	
	
	@RequestMapping(value = "/uploadImg", method = {RequestMethod.POST,RequestMethod.GET})
	public String processImageUpload(@RequestParam() MultipartFile file) throws Exception {
		if (!file.isEmpty()) {
		/*	byte[] bytes = file.getBytes();
			FileOutputStream fos = new FileOutputStream(Config.USER_ICON_DIR +""+ Config.ORGINAL_ICON_DIR + file.getOriginalFilename()); // 上传到写死的上传路径
			fos.write(bytes); // 写入文件
*/		}
		System.out.println("name: " + file.getOriginalFilename() + "  size: " + file.getSize()); // 打印文件大小和文件名称
		return "redirect:/image"; // 跳转你所指定的页面名称
	}

	/**
	 * 查询用户信息
	 * 
	 * @see 访问该方法的路径就应该是"/user/具体的用户id"
	 * @see 这里value="/{username}"的写法，需要格外注意一下，它是一个路径变量，此时用来接收前台的一个资源
	 * @see 这时value="/{username}"就会到方法参数中找@PathVariable String
	 *      username，并将路径变量值传给username参数
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String show(@PathVariable long id, Model model) {
		User user = userService.findOne(id);
		model.addAttribute("user", user);
		return viewName("show");
	}

	/**
	 * 编辑用户信息
	 * 
	 * @see 访问该方法的路径就应该是"/user/具体的用户名/update"
	 */
	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String update(@PathVariable long id, Model model) {
		User user = userService.findOne(id);
		model.addAttribute("user", user);
		return viewName("update");
	}	

}
