import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dream.common.constants.ErrorCode;



public class JsonTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
    JSONObject jsonObject = new JSONObject();
    List<Object> list = new ArrayList<Object>();
    for(int i=1; i<5; i++){
      list.add("url"+i);
    }
    JSONArray jsonArray = new JSONArray(list);
    /*jsonArray.add("url1");
    jsonArray.add("url2");
    jsonArray.add("url3");
    jsonArray.add("url4");*/
    String[] str = {"url1","url2","url3"};
    jsonObject.put("imgUrl", null);
    jsonObject.put("return", ErrorCode.SUCCESS);
    System.out.println(jsonObject.toJSONString()); //{"ImgUrl":["url1","url2","url3","url4"]}
    System.out.println(list.toString());
  }

}
