package com.dream.web.service.user;

public class Config {

    // Should be assign value when db init
    public static String SERVER_ID;
    
    public static final String JPUSH_MASTER_SECRET = "26ca300f8070f26c20a58ce3";
    public static final String JPUSH_APPKEY = "88cecbe4038083e25ccd6c17";
    
    public static final String VERSION = "0.1.0";
    public static final String TAG = "all";
    public static final String  TASK_CACHE= "freshTaskCache";
    public static final String USER_CACHE = "userCache";
    public static final int CACHE_DURATION_DAYS = 7;
    public static final int MESSAGE_CACHE_MAX_NUMBER = 100;
    public static final int RECENT_CHATS_CACHE_MAX_NUMBER = 10;
    public static final int THREAD_MAX_NUMBER=10;
    
    public static final String ORGINAL_ICON_DIR = "orginalPic";  //保存原始图片的文件夹
    
    public static final String COMPRESS_ICON_DIR = "compressPic"; //保存缩略图片文件夹
    
    public static final String USER_ICON_DIR = "G:/upload/portrait";  //保存原始图片的文件夹
    
    public static final String TASK_ICON_DIR = "G:/upload/task"; //保存缩略图片文件夹
    
    public static final int COMPRESS_TASK_ICON_WIDTH = 120; 
    public static final  int COMPRESS_TASK_ICON_HEIGHT = 120; 
    
    public static final int COMPRESS_USER_ICON_WIDTH = 80; 
    public static final  int COMPRESS_USER_ICON_HEIGHT = 80; 
    
    public static final int LOADING_TASK_COUNT = 10;
    
    public static final int INIT_TASK_COUNT__CACHE = 100;
    
    
}

