package com.dream.web.quartz.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import net.sf.ehcache.Cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dream.common.entity.UserTask;
import com.dream.common.inject.annotation.BaseComponent;
import com.dream.web.cache.factory.EcacheFactory;
import com.dream.web.service.user.Config;
import com.dream.web.service.user.TaskService;


public class QuartzJob {
  private static ThreadPool threadPool = new ThreadPool(Config.THREAD_MAX_NUMBER); 
  final Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  @BaseComponent
  private  TaskService taskService;
  
  
  @SuppressWarnings("unchecked")
  public void work()  {
    //addTask();
    Cache cache = EcacheFactory.getCacheInstance().getCache(Config.TASK_CACHE);
    logger.debug("当前有 "+cache.getSize()+" 条任务！");
    if(cache != null && cache.getSize() > 0){
      List<Object> keys = cache.getKeys();
      Iterator<Object> iter = keys.iterator();
      while(iter.hasNext()){
        Object key = iter.next();
        UserTask t = (UserTask)cache.get(key).getObjectValue();
        int flag = 0;
        try {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
          flag = compareTime(sdf.parse(t.getEndtime()));
        } catch (ParseException e) {
          e.printStackTrace();
        }  //-1：未过期  1：已过期
        if(flag == 1){ //任务过期则删除该任务
          logger.debug("从缓存和数据库删除任务:  "+key);
          threadPool.execute(deleteTask(t, this.taskService));
          cache.acquireWriteLockOnKey(key);
          cache.remove(key);
          cache.releaseWriteLockOnKey(key);
        }
      }
    } 
    logger.debug("轮询后还剩 "+cache.getSize()+" 条任务！");
}
  
  private int compareTime(Date endDate){
   Date currentDate = new Date();
   return currentDate.compareTo(endDate);
  }
  
  private static Runnable deleteTask(final UserTask bean, final TaskService taskService) {
    return new Runnable() {
        public void run() { 
          System.out.println(" id ："+bean.getId()+"的任务時效到了，从数据库中删除。");
          bean.setStatus(1);  //1：代表任务过时
          taskService.update(bean);
        }
    };
}
  
}
