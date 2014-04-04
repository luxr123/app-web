package com.dream.web.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional
  public UserTask save(UserTask task) {
    UserTask t = super.save(task);
    EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, t.getId(), t);
    return t;
  }

  @Transactional
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
  
  public List<UserTask> findByLastId(long lastTaskId,final  int spare){
    Pageable pageable = new Pageable() {
      
      @Override
      public Pageable previousOrFirst() {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public Pageable next() {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public boolean hasPrevious() {
        // TODO Auto-generated method stub
        return false;
      }
      
      @Override
      public Sort getSort() {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public int getPageSize() {
        // TODO Auto-generated method stub
        return spare;
      }
      
      @Override
      public int getPageNumber() {
        // TODO Auto-generated method stub
        return 0;
      }
      
      @Override
      public int getOffset() {
        // TODO Auto-generated method stub
        return 0;
      }
      
      @Override
      public Pageable first() {
        // TODO Auto-generated method stub
        return null;
      }
    };
    return taskRepository.findByLastId(lastTaskId, pageable);
  }
  
  public int getSuccessCount(long taskId){
    return taskRepository.getSuccessCount(taskId);
  }
  
  public int getWaitCount(long taskId){
    return taskRepository.getWaitCount(taskId);
  }
  
  public List<UserTask> findAllByUserId(long userId){
    return taskRepository.findAllByUserId(userId);
  }
  
  public List<UserTask> findLiveByUserId(long userId, long beginId){
    return taskRepository.findLiveByUserId(userId, beginId);
  }

  public List<UserTask> loadPartTask(Pageable pageable) {
    return taskRepository.loadPartTask(pageable);
  }
}
