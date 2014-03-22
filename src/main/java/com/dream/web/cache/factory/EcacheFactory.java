package com.dream.web.cache.factory;


import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EcacheFactory{
  
  private static final String PATH = "/config/spring-config-cache.xml";
  private  CacheManager manager;
  private static EcacheFactory ecacheFactory;
  
  public EcacheFactory(String path){
    // manager = CacheManager.create(path);
  
      Resource res = new ClassPathResource(path);
      BeanFactory factory = new XmlBeanFactory(res);
      manager = (CacheManager) factory.getBean("ehcacheManager");
      System.out.println("--------当前cacheManager中的cache数量为： "+manager.getCacheNames().length); 
  }
  
  public static EcacheFactory getCacheInstance(){
    if(ecacheFactory == null){
      ecacheFactory = new EcacheFactory(PATH);  
    }
    return ecacheFactory;
  }
  
  public void put(String cacheName, Object key, Object value){
    Cache cache = manager.getCache(cacheName);
    if(cache == null){
      cache = new Cache(cacheName,5000,false,false,600,0);
      manager.addCache(cache);
    }
    Element element = new Element(key, value);
    cache.put(element);
  }
  
  public Object getElement(String cacheName, Object key){
    Cache cache = manager.getCache(cacheName);
    if(cache != null){
      Element element = cache.get(key);
      return element == null ? null : element.getObjectValue();
    }
    return null;
  }
  
  public void remvoeElement(String cacheName, Object key){
    Cache cache = manager.getCache(cacheName);
    if(cache != null){
      cache.remove(key);
    }
  }
  
  public Cache getCache(String cacheName){
    Cache cache = manager.getCache(cacheName);
    return cache;
  }
  
  public void removeCache(String cacheName){
    manager.removeCache(cacheName);
  }
 
}
