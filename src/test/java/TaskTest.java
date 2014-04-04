
import java.util.Iterator;
import java.util.List;

import com.dream.common.entity.UserTask;
import com.dream.web.cache.factory.EcacheFactory;
import com.dream.web.controller.user.TaskController;
import com.dream.web.service.user.Config;



public class TaskTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
  /*  for(int i=3;i<8;i++){
      UserTask task = new UserTask();
      task.setId(Long.parseLong(i+""));
      task.setContent("hello_"+i);
      task.setCreateuserid(1l);
      EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, task.getId(),task);
    }
    TaskController controller = new TaskController();
    List<UserTask> list = controller.getFirstPage("0");
    Iterator<UserTask> iter = list.iterator();
    while(iter.hasNext()){
      UserTask task = iter.next();
      System.out.println(task.getId()+" : "+task.getContent());
    }
*/
    
    UserTask task = new UserTask();
    task.setContent("11");
    
    UserTask t = task;
    t.setContent("12");
    
    System.out.println(task.getContent()); //12而不是11
    
    UserTask t2 = new UserTask();
    t2 = task;
    t2.setContent("13");
    System.out.println(task.getContent()); //13而不是11
  }

}
