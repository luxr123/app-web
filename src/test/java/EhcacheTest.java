import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.h2.util.CacheObject;
import org.hibernate.validator.internal.util.privilegedactions.NewInstance;
import org.junit.internal.runners.model.EachTestNotifier;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.dream.web.cache.factory.EcacheFactory;
import com.dream.web.service.user.Config;

public class EhcacheTest {
  
 // private static CacheManager ehcacheManager;
 //URL url = this.getClass().getResource("config/spring-config-cache.xml");
  
  //CacheManager ehcacheManager = CacheManager.newInstance("/spring-config-cache.xml");
  public static void main(String[] args) {
    //CacheManager cacheManager = CacheManager.create("/config/spring-config-cache.xml");
  /*  Resource res = new ClassPathResource("/config/spring-config-cache.xml");
    BeanFactory factory = new XmlBeanFactory(res);
    CacheManager cacheManager = (CacheManager) factory.getBean("ehcacheManager");*/
   // EhcacheTest e = new EhcacheTest();
  /*  InputStream fis = null;
    try {
      fis = new FileInputStream(new File("src/main/resources/config/spring-config-cache.xml").getAbsolutePath());
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }*/
    //System.out.println(e.url);
    CacheManager ehcacheManager = CacheManager.create();
    String[] cacheNames = CacheManager.getInstance().getCacheNames();
    System.out.println(cacheNames.length);
    Cache levelOneCache  = new Cache("loginRecordCache",100,false,false,600,0);
    ehcacheManager.addCache(levelOneCache);
    cacheNames = CacheManager.getInstance().getCacheNames();
    System.out.println(cacheNames.length);
   // Cache levelOneCache = ehcacheManager.getCache("loginRecordCache");
    //Cache levelOneCache =EcacheFactory.getCacheInstance().getCache(Config.TASK_CACHE);
    String str = null;
    for (int i = 0; i < 120; i++) {
      str = "hello_" + i;
      Element element = new Element("key" + i, str);
      levelOneCache.put(element);
    }
    Element element = levelOneCache.get("key" + 92);
    if (element != null) {
      System.out.println("cache[" + element.getObjectValue() + "]" + ",从缓存中取到");
    }
    System.out.println(levelOneCache.getSize());
    levelOneCache.remove("key" + 92);
    Element element2 = levelOneCache.get("key" + 92);
    if (element2 == null) {
      System.out.println("cache" + ",无法从缓存中取到");
    }
    System.out.println(levelOneCache.getSize());
    ehcacheManager.shutdown();
  }
}
