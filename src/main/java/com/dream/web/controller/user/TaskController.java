package com.dream.web.controller.user;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dream.common.constants.ErrorCode;
import com.dream.common.controller.BaseController;
import com.dream.common.entity.User;
import com.dream.common.entity.UserTask;
import com.dream.common.inject.annotation.BaseComponent;
import com.dream.web.cache.factory.EcacheFactory;
import com.dream.web.service.user.Config;
import com.dream.web.service.user.TaskService;
import com.dream.web.service.user.UserService;
import com.dream.web.util.TaskUtil;
import com.google.gson.JsonParser;

@Controller
@RequestMapping("/task")
public class TaskController extends BaseController<UserTask, Long> {

  final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private TaskService taskService;
  
  @Autowired
  @BaseComponent
  private UserService userService;

  /**
   * 
   * @param userTaskJson  用户任务对象
   * @param files  图片文件流
   * @return
   */
  @RequestMapping(value = "/addTask", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
  public @ResponseBody String addTask(@RequestParam("userTask")
  String userTaskJson, @RequestParam("files")   MultipartFile[] files) {
    UserTask userTask = JSONObject.parseObject(userTaskJson, UserTask.class);
    userTask.setId(0l); //json转换long型时变成了Integer所以需要这样显示的转成long
    logger.debug(userTaskJson);
     List<Object> value = new ArrayList<Object>();
    if (files != null && files.length > 0){
      String imgUrls = "";
      for (MultipartFile file : files) {
        if (!file.isEmpty()) {
          String path = TaskUtil.saveImgFile(file, Config.TASK_ICON_DIR);  //保存原图
          String compressPath = TaskUtil.compressPic(path, Config.COMPRESS_TASK_ICON_WIDTH, Config.COMPRESS_TASK_ICON_HEIGHT);  //形成缩略图
          imgUrls += compressPath + ";" ;  //最终存入数据库的路径指向缩略图
          value.add(compressPath);
        }
      }
      userTask.setImgurl(imgUrls);
    }  
    userTask.setStatus(0);// status:0 没过时
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String currDate = sdf.format(new Date());
    userTask.setCreatetime(currDate);
    UserTask t = taskService.save(userTask);
    if (t != null && t.getId() != null) {
      t.setSuccesscount(0);
      t.setWaitcount(0);
      EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, t.getId(), t);
      return TaskUtil.toJSONString("imgUrl", value, ErrorCode.SUCCESS); 
    }
    return TaskUtil.toJSONString("imgUrl", null, ErrorCode.FAIL); 
  }
  
  
  /**
   * 首次加载页面和手指下拉刷新整个页面
   * @param maxId  传客户端最大的任务id过来
   * @return       
   */
  @RequestMapping(value = "/getFirstPage", method = RequestMethod.POST)
  public @ResponseBody List<UserTask> getFirstPage(@RequestParam("maxId") String maxId, @RequestParam("oldestId") String oldestId){
    long key = Long.parseLong(maxId);
    List<Long> list = getSortList();
    int length = list.size();  //当前缓存中的任务个数
    int index = binarySearch(list, key, 0, length-1);//找到需要发送的任务开始位置
    if(list.get(index).compareTo(key) == 0){
      index++;   //要从下一个开始拿最新的
    }
    List<UserTask> loadList = new ArrayList<UserTask>();
    int loadCount = (length - index)>=Config.LOADING_TASK_COUNT ? Config.LOADING_TASK_COUNT : (length - index);
    for(int i =1; i<= loadCount; i++){
      UserTask task =((UserTask)EcacheFactory.getCacheInstance().getElement(Config.TASK_CACHE, list.get(length-i)));
      User user = userService.findOne(task.getCreateuserid());
      task.setCreateusername(user.getName());
      task.setUserIcon(user.getIconPath());
      loadList.add(task);
    }
    if( loadCount < Config.LOADING_TASK_COUNT && index>0){
      for(int i=1; i<=(Config.LOADING_TASK_COUNT- loadCount) && index-i >=0;  i++){
        UserTask task = getTask(list.get(index-i), Long.parseLong(oldestId));
        User user = userService.findOne(task.getCreateuserid());
        task.setCreateusername(user.getName());
        task.setUserIcon(user.getIconPath());
        loadList.add(task);        
      }
    }
    return loadList;
  }
  
  /**
   * 用户上拉刷新加载任务每次10条
   * @param currentId  上次加载后listview中显示的任务最后一条如 id： 30~10  就传10过来 ；id越大就越显示在前面
   * @param oldestId；数据库中保存的最小的id，也意味着最老的任务id，传过来。这个id基本上在客户端一旦确定不会再变了
   * @return     
   */
  @RequestMapping(value = "/getNextPage", method = RequestMethod.POST )
  public @ResponseBody List<UserTask> getNextPage(@RequestParam("currentId") String currentId, @RequestParam("oldestId") String oldestId){
    long key = Long.parseLong(currentId);
    List<Long> list = getSortList();
    int length = list.size();  //当前缓存中的任务个数
    int index = binarySearch(list, key, 0, length-1);//找到需要发送的任务开始位置
    List<UserTask> loadList = new ArrayList<UserTask>();
    int spare = Config.LOADING_TASK_COUNT - index;
    int count = spare >0 ? index : Config.LOADING_TASK_COUNT;
    if(index>0){//从缓存中拿
      for(int i =1; i<=count; i++){
        UserTask task = getTask(list.get(index-i), Long.parseLong(oldestId));
        User user = userService.findOne(task.getCreateuserid());
        task.setCreateusername(user.getName());
        task.setUserIcon(user.getIconPath());
        loadList.add(task);
      } 
    }
  if(spare > 0){ //从数据库拿
    long taskId = 0l;    
    if( index == 0){
      taskId = key;
    }else if(index > 0){
      taskId = list.get(0);
    }
    List<UserTask> spareTasks = taskService.findByLastId(taskId, spare);
    Iterator<UserTask> iter = spareTasks.iterator();
    while(iter.hasNext()){
      UserTask task = iter.next();
      if(task.getId().compareTo(Long.parseLong(oldestId)) >=0){
        task = slimming(task);
      }
      User user = userService.findOne(task.getCreateuserid());
      task.setCreateusername(user.getName());
      task.setUserIcon(user.getIconPath());      
      int successCount = taskService.getSuccessCount(task.getId());
      int waitCount = taskService.getWaitCount(task.getId());
      task.setSuccesscount(successCount);
      task.setWaitcount(waitCount);
      loadList.add(task);
    }
  }    
    return loadList;
  }
  
  /**
   * 获得指定用户全部任务
   * @param userid
   * @return  
   */
  @RequestMapping(value = "/getAllUserTasks", method = RequestMethod.POST)
  public @ResponseBody List<UserTask> getAllUserTasks(@RequestParam("userid") String userid ){
    //从数据库拿
    List<UserTask> tasks = taskService.findAllByUserId(Long.parseLong(userid));
    return tasks;
  }
  
  /**
   * 获得指定用户还存活的任务,即还未过期的任务
   * @param userid 
   * @return    
   */
  @RequestMapping(value = "/getUserLiveTasks", method = RequestMethod.POST)
  public @ResponseBody List<UserTask> getUserLiveTasks(@RequestParam("userid") String userid){
    long id = Long.parseLong(userid);
    List<UserTask> tasks = new ArrayList<UserTask>();
    User user = userService.findOne(id);
    //先从缓存里拿
    List<Long> keys = getSortList();
    Iterator<Long> iter = keys.iterator();
    while(iter.hasNext()){
      long key = iter.next();
      UserTask task = (UserTask) EcacheFactory.getCacheInstance().getElement(Config.TASK_CACHE, key);
      if(id == task.getCreateuserid()){
        task.setCreateusername(user.getName());
        task.setUserIcon(user.getIconPath());
        tasks.add(0,task);  //让最新的任务放在前面
      }
    }
  //再从数据库拿
    long minId = keys.get(0);  //从哪里起始拿
    tasks.addAll(taskService.findLiveByUserId(id, minId));
    return tasks;
  }
  
  
  /**
   * 获得一张压缩图片
   * @param compressPath 压缩图片的路径，即就是数据库中所存的路径。
   * @return   
   */
  @RequestMapping(value = "/getCompressPic" , method = RequestMethod.POST)
  public @ResponseBody String getCompressPic(@RequestParam("url") String compressPath){       
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
   * @param compressPath 压缩图片路径，即就是数据库中所存的路径。拿到后会做下转换，为原图路径
   * @return     
   */
  @RequestMapping(value = "/getOrginalPic", method = RequestMethod.POST)
  public @ResponseBody String getOrginalPic(@RequestParam("url") String compressPath) {
    String orginalPath = TaskUtil.convertPath(compressPath, Config.ORGINAL_ICON_DIR);  //转换为原图路径
    String fileStream = null;
    try {
      fileStream = TaskUtil.iconToByte(orginalPath);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return fileStream;
  }
  

  
 //----------------------调用的方法-------------------------- 
  
  
  
  
  /**
   * 返回找到的key所在的位置或者第一个大于该key的位置（key不存在了的时候）
   * @param list
   * @param key
   * @param low
   * @param high
   * @return
   */
  public  int binarySearch(List<Long>  list, long key, int low, int high){
    if(low <= high){
      int mid = low + (high - low)/2;
      int result = list.get(mid).compareTo(key);
      if(result<0) return binarySearch(list, key, mid+1, high );
      else if(result>0) return  binarySearch(list, key, low , mid-1 );
      else if(result == 0){
        return mid;
      }
    }    
   return low;
  }
  
  /**
   * 给任务对象瘦身
   * @param task
   * @return
   */
  private  UserTask slimming(UserTask task){
    task.setContactinfo(null);
    task.setContent(null);
    task.setCreatetime(null);
    task.setEndtime(null);
    return task;
  }
  
  /**
   * 将获得的taskid进行排序，默认是taskid越大，则越新
   * @return
   */
  @SuppressWarnings("unchecked")
  private List<Long> getSortList(){
    List<Long>  keys = EcacheFactory.getCacheInstance().getCache(Config.TASK_CACHE).getKeys();
    List<Long> list = new ArrayList<Long>();
    list.addAll(keys);
    Collections.sort(list);  //对任务id根据大小排序  默认id大的是最新的   
    return list;
  }
  
  /**
   * 从缓存取对象，通过与最小客户端任务id比较而决定拿克隆对象还是直接拿对象。
   * 因为不拿克隆对象，在后面对对象进行slimming（UserTask）操作时，对应的该缓存对象也会跟着变化，所以需拿克隆对象。
   * @param listKey
   * @param oldestId
   * @return
   */
  private UserTask getTask(long listKey, long oldestId){
    UserTask task = null;
    if(listKey >= oldestId){
      task = slimming( (UserTask)EcacheFactory.getCacheInstance().cloneElement(Config.TASK_CACHE, listKey));     //拿克隆缓存对象。
    }else{
      task =  (UserTask)EcacheFactory.getCacheInstance().getElement(Config.TASK_CACHE, listKey);
    }
    return task;
  }
  
  
}
