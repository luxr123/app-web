package com.dream.web.repository.user;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.dream.common.entity.UserTask;
import com.dream.common.repository.BaseRepository;


public interface TaskRepository extends BaseRepository<UserTask, Long>{
  
  @Query("select t from UserTask t  where t.status=0 and t.id < ?1  order by t.id desc")
  List<UserTask> findByLastId(long id ,Pageable pageable);
  
  @Query("select count(*) from Status s where s.status=1 and s.taskid=?1")
  int getSuccessCount(long id);
  
  @Query("select count(*) from Status s where s.status=0 and s.taskid=?1")
  int getWaitCount(long id);
  
  @Query("select t from UserTask t where  t.createuserid=?1 and t.status <> 2")
  List<UserTask> findAllByUserId(long userId);
  
  @Query("select t from UserTask t where t.status=0 and t.createuserid=?1 and t.id < ?2 order by t.id desc")
  List<UserTask> findLiveByUserId(long userId, long beginId);

  //启动时加载最新的任务信息
  @Query("select t from UserTask t  where t.status=0 order by t.id desc")
  List<UserTask> loadPartTask(Pageable pageable);
}
