package com.dream.web.service.user;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dream.common.entity.UserTask;
import com.dream.common.inject.annotation.BaseComponent;
import com.dream.common.service.BaseService;
import com.dream.web.cache.factory.EcacheFactory;
import com.dream.web.repository.user.TaskRepository;

@Service
public class TaskService extends BaseService<UserTask, Long> {

  @Autowired
  @BaseComponent
  public TaskRepository taskRepository;

  public UserTask save(UserTask task) {
    if (task.getCreatetime() == null) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      String currDate = sdf.format(new Date());
      task.setCreatetime(currDate);
    }
    UserTask t = super.save(task);
    EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, t.getId(), t);
    return t;
  }

  public UserTask update(UserTask task) {
    UserTask t = super.update(task);
    if (t != null) {
      long id = t.getId();
      if (t.getStatus() == 0) {
        EcacheFactory.getCacheInstance().getCache(Config.TASK_CACHE).acquireWriteLockOnKey(id);
        EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, id, t);
        EcacheFactory.getCacheInstance().getCache(Config.TASK_CACHE).releaseWriteLockOnKey(id);
      }
    }
    return t;
  }
  
  public UserTask findOne(long id) {
    UserTask task = (UserTask)EcacheFactory.getCacheInstance().getElement(Config.TASK_CACHE, id);
    if(task != null){
      return task;
    }
    task= taskRepository.findOne(id);
    EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, id, task);
    return task;
  }
}
