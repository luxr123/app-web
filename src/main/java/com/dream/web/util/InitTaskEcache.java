package com.dream.web.util;

import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.dream.common.entity.UserTask;
import com.dream.web.cache.factory.EcacheFactory;
import com.dream.web.service.user.Config;
import com.dream.web.service.user.TaskService;


public class InitTaskEcache {
  
  public InitTaskEcache(TaskService taskService){
    
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
               return 10;
          // return Config.INIT_TASK_COUNT__CACHE;
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
    List<UserTask> list = taskService.loadPartTask(pageable);
    Iterator<UserTask> iter = list.iterator();
    while(iter.hasNext()){
      UserTask t = iter.next();
      EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, t.getId(), t);
    
    }

  
  }

    
   
}
