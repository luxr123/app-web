import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dream.web.cache.factory.EcacheFactory;
import com.dream.web.service.user.Config;



public class CahcheTest {
   public static void main(String[] args){
     EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, 1l, "h1");
     EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, 2l, "h2");
     EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, 5l, "h5");
     EcacheFactory.getCacheInstance().put(Config.TASK_CACHE,  7l, "h7");
     EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, 8l, "h8");
     
     EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, 10l, "h10");
     EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, 12l, "h12");
     EcacheFactory.getCacheInstance().put(Config.TASK_CACHE,  14l, "h14");
     EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, 15l, "h15");
     
     EcacheFactory.getCacheInstance().put(Config.TASK_CACHE, 17l, "h17");
     EcacheFactory.getCacheInstance().put(Config.TASK_CACHE,  20l, "h20");
     List<Long> list = new ArrayList<Long>();
     List<Long>  keys = EcacheFactory.getCacheInstance().getCache(Config.TASK_CACHE).getKeys();
     int size = EcacheFactory.getCacheInstance().getCache(Config.TASK_CACHE).getSize();
     //System.out.println(size);
     list.addAll(keys);
     Collections.sort(list);
   /*  for(int i =0; i<nnnn.size(); i++){
       System.out.println(EcacheFactory.getCacheInstance().getElement(Config.TASK_CACHE, nnnn.get(i)) );
     }*/
     System.out.println("拿4个 : ");
       for(int i =3; i<7; i++){
     System.out.println( "pop:  "+ list.get(list.size()-i));
   }
       
     int index1 = search(list, 10l, 0, size-1);
     System.out.println(list.get(index1));
     
     System.out.println("接着加载5个 : ");
     for(int i =1; i<6; i++){
   System.out.println( "pop:  "+ list.get(index1-i));
 }
     
     
     int index2 = search(list, 16l, 0, size-1);
     System.out.println(list.get(index2));
     if(list.size()-index2 < 4) {
       System.out.println("不足4个");
       System.out.println("刷新拿前"+(list.size()-index2-1)+"个 : ");
       for(int i =1; i<3; i++){
         System.out.println( "pop:  "+ list.get(list.size()-i));
   }
       System.out.println("刷新拿后2个补上4个之数");
       for(int i =0; i<2; i++){
         System.out.println( "pop:  "+ list.get(index2-i));
   }
     }
    
     
   }
   
   public static int search(List<Long>  list, long key,int low, int high){
     if(low <= high){
       int mid = low + (high - low)/2;
       int result = list.get(mid).compareTo(key);
       if(result<0) return search(list, key, mid+1, high );
       else if(result>0) return  search(list, key,low , mid-1 );
       else if(result == 0){
         return mid;
       }
     }    
    return low;
   }
}
