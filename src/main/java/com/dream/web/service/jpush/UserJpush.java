package com.dream.web.service.jpush;


import java.util.HashMap;
import java.util.Map;


import com.dream.common.entity.User;
import com.dream.web.service.user.Config;

import cn.jpush.api.JPushClient;
import cn.jpush.api.MessageResult;


public class UserJpush {
  private static final long serialVersionUID = 348660245631638687L;
  //private static Logger LOG = Logger.getLogger(UserJpush.class);
  private static int sendId = getRandomSendNo();
  private static final JPushClient jpushClient = new JPushClient(
              Config.JPUSH_MASTER_SECRET,Config.JPUSH_APPKEY);
  public static final int MAX = Integer.MAX_VALUE / 2;
  public static final int MIN = MAX / 2;

  /**
   * 保持 sendNo 的唯一性是有必要的
   * It is very important to keep sendNo unique.
   * @return sendNo
   */
  public static int getRandomSendNo() {
      return (int) (MIN + Math.random() * (MAX - MIN));
  }
  
  public static void loginJpush(String udid, User user){
    MessageResult msgResult = null;
    String myName = user.getName();
    String content = "用户 "+myName+"上线啦！";
    sendId ++;
    Map<String, Object> extra = new HashMap<String, Object>();
    extra.put("Tags", Config.TAG);
    extra.put("sendNo", sendId);
    msgResult = jpushClient.sendCustomMessageWithTag(sendId,Config.TAG,  myName, content, null, extra);
    String info = "";
    if (null == msgResult) {
       info = "Unexpected: null result.";       
        
    } else if (msgResult.getErrcode() != 0) {
        info = "Send msg error - errorCode:" + msgResult.getErrcode()
                + ", errorMsg:" + msgResult.getErrmsg()
                + ", sendno:" + msgResult.getSendno();
    }else{
        info = "====jpush success=======";
    }
    System.out.println(info);
  }
}

