package com.dream.web.controller.user;

import java.util.ArrayList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONObject;
import com.dream.common.constants.ErrorCode;
import com.dream.common.controller.BaseController;
import com.dream.common.entity.UserTask;
import com.dream.common.inject.annotation.BaseComponent;
import com.dream.web.cache.factory.EcacheFactory;
import com.dream.web.service.user.Config;
import com.dream.web.service.user.TaskService;
import com.dream.web.service.user.UserService;
import com.dream.web.util.TaskUtil;

@Controller
@RequestMapping("/task")
public class TaskController extends BaseController<UserTask, Long> {

  final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  @BaseComponent
  private TaskService taskService;
  
  @Autowired
  private UserService userService;

  @RequestMapping(value = "/addTask", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
  public @ResponseBody ErrorCode addTask(@RequestParam("userTask")
  String userTaskJson, @RequestParam("files")   MultipartFile[] files) {
    UserTask userTask = JSONObject.parseObject(userTaskJson, UserTask.class);
    userTask.setId(0l); //json转换long型时变成了Integer所以需要这样显示的转成long
    logger.debug(userTaskJson);
    if (files != null && files.length > 0){
      String imgUrls = "";
      for (MultipartFile file : files) {
        if (!file.isEmpty()) {
          String path = TaskUtil.saveImgFile(file, Config.TASK_ICON_DIR);
          TaskUtil.compressPic(path, Config.COMPRESS_TASK_ICON_WIDTH, Config.COMPRESS_TASK_ICON_HEIGHT);  //形成缩略图
          imgUrls += path + ";" ;
        }
      }
      userTask.setImgurl(imgUrls);
    }  
    userTask.setStatus(0);// status:0 没过时
    UserTask t = taskService.save(userTask);
    if (t != null && t.getId() != null) {
      EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, t.getId(), t);
    }
    return ErrorCode.SUCCESS;
  }
  
  
  /**
   * 获得某一任务的压缩图片
   * @param id  任务id
   * @return
   */
  @RequestMapping(value = "/getCompressPics", method = RequestMethod.POST)
  public @ResponseBody List<String> getCompressPics(@RequestParam("id") String id){
    UserTask task = taskService.findOne(Long.parseLong(id));
    List<String> fileDataList = null;
    if(task != null && task.getImgurl() != null){
      String iconPath = task.getImgurl(); //图片路径之间通过“；”隔离开
      String[] icons = iconPath.split(";");
      fileDataList = new ArrayList<String>();
      for(int i = 0; i<icons.length;i++){
        String inputPath = icons[i];
        String compressPath = TaskUtil.convertPath(inputPath, Config.COMPRESS_ICON_DIR);
        fileDataList.add(TaskUtil.iconToByte(compressPath));      
      }
    }
   return fileDataList;
    
   
}
  /**
   * 获得源图片
   * @param id 任务id
   * @param name  图片名称
   * @return
   */
  
  @RequestMapping(value = "/getOrginalPic", method = RequestMethod.POST)
  public @ResponseBody String getOrginalPic(@RequestParam("id") String id, @RequestParam("name") String name) {
    UserTask task = taskService.findOne(Long.parseLong(id));
    String fileStream = null;
    if(task != null && task.getImgurl() != null){      
      String path = task.getImgurl().split(";")[0];
      String prePath = path.substring(0,path.lastIndexOf("/"));
      String  orginalPath = prePath.substring(0,prePath.lastIndexOf("/")+1) + Config.ORGINAL_ICON_DIR + name;
      fileStream = TaskUtil.iconToByte(orginalPath);
    }
    return fileStream;
  }

}
