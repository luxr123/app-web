package com.dream.web.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.dream.common.constants.ErrorCode;
import com.dream.web.service.user.Config;
import com.sun.image.codec.jpeg.JPEGCodec;  
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import encode.BASE64Encoder;


@SuppressWarnings("restriction")
public class TaskUtil {
  
  final Logger logger = LoggerFactory.getLogger(getClass());

  /*  
   * 获得图片大小  
   * 传入参数 String path ：图片路径  
   */   
  public static long getPicSize(String path) {   
      File file = new File(path);   
      return file.length();   
  }  
    
  /**
   * 
   * @param inputPath  源路径
   * @param outputPath  输出路径
   * @param outputWidth  输出文件长
   * @param outputHeight  输出文件高
   * @param gp   是否等比缩放
   */
  public static  void compressPic(String inputPath, String outputPath, int outputWidth, int outputHeight, boolean gp) {   
      try {   
          //获得源文件   
          File file = new File(inputPath);   
          if (!file.exists()) {   
            //logger.error("源文件不存在！"); 
            throw new RuntimeException("源文件不存在！");
          }   
          Image img = ImageIO.read(file);   
          // 判断图片格式是否正确   
          if (img.getWidth(null) == -1) { 
             // logger.error("can't read,retry!");
            throw new RuntimeException("can't read,retry!");
          } else {   
              int newWidth; int newHeight;   
              // 判断是否是等比缩放   
              if (gp == true) {   
                  // 为等比缩放计算输出的图片宽度及高度   
                  double rate1 = ((double) img.getWidth(null)) / (double) outputWidth + 0.1;   
                  double rate2 = ((double) img.getHeight(null)) / (double) outputHeight + 0.1;   
                  // 根据缩放比率大的进行缩放控制   
                  double rate = rate1 > rate2 ? rate1 : rate2;   
                  newWidth = (int) (((double) img.getWidth(null)) / rate);   
                  newHeight = (int) (((double) img.getHeight(null)) / rate);   
              } else {   
                  newWidth = outputWidth; // 输出的图片宽度   
                  newHeight = outputHeight; // 输出的图片高度   
              }   
             BufferedImage tag = new BufferedImage((int) newWidth, (int) newHeight, BufferedImage.TYPE_INT_RGB);   
               
             /* 
              * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 
              * 优先级比速度高 生成的图片质量比较好 但速度慢 
              */   
             tag.getGraphics().drawImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);  
             FileOutputStream out = new FileOutputStream(outputPath);  
             //JPEGImageEncoder可适用于其他图片类型的转换   
             JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);   
             encoder.encode(tag);   
             out.close();   
          }   
      } catch (IOException ex) {   
          ex.printStackTrace();   
      }   
 }   
  
  
  /**
   * 保存该原图的缩略图
   * @param inputPath
   * @param outputWidth
   * @param outputHeight
   * @throws IOException
   */
  public static String compressPic(String inputPath, int outputWidth, int outputHeight) {  
    String outputPath = convertPath(inputPath,Config.COMPRESS_ICON_DIR);
    compressPic(inputPath, outputPath, outputWidth, outputHeight, true);
    return outputPath;
}
  
  /**
   * 路径转换
   * 通过原路径拿取缩略图路径，通过改变最后一层的文件夹名，其余都一样  
   * 通过缩略图拿取原图路径，原理同上
   * @param inputPath
   * @param dirName  欲拿取所在文件的最后一层文件夹名
   * @return
   */
  public static String convertPath(String inputPath, String  dirName){
    String name = inputPath.substring(inputPath.lastIndexOf("/")+1);
    String prePath = inputPath.substring(0,inputPath.lastIndexOf("/"));
    String outputPath = prePath.substring(0,prePath.lastIndexOf("/")+1)+dirName;  
    File dir = new File(outputPath);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    outputPath += "/"+name;
    return outputPath;
  }
  
  /**
   * 返回图片流
   * @param path  文件路径
   * @return fileStr
   * @throws IOException 
   */
  public static String iconToByte(String path) throws IOException{
    Resource res = new FileSystemResource(path);
    byte[] fileData = FileCopyUtils.copyToByteArray(res.getInputStream());
    BASE64Encoder encoder = new BASE64Encoder();
    return encoder.encode(fileData);// 返回Base64编码过的字节数组字符串
  }
  
  /**
   * 保存原图片并返回路径
   * @param imgFile
   * @return
   */
    public static String saveImgFile(MultipartFile imgFile, String prePath) {
      String filePath = "";
      if (imgFile.getSize() > 0) {
        FileOutputStream fos = null;
        try {
          byte[] b = imgFile.getBytes();
          /* 构造文件路径 */
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
          SimpleDateFormat sdf2 = new SimpleDateFormat("HHmmssSSS");
          String datePath = sdf.format(new Date());
          String picName = sdf2.format(new Date());
          String dirPath = prePath +"/"+ datePath+"/"+Config.ORGINAL_ICON_DIR;
          File dir = new File(dirPath);
          if (!dir.exists()) {
            dir.mkdirs();
          }
          String suffix = imgFile.getOriginalFilename().split("\\.")[1];
          filePath = dirPath + "/" + picName + "." + suffix;
          File file = new File(filePath);

          dir.setWritable(true);
          file.setWritable(true);

          fos = new FileOutputStream(file);
          fos.write(b);

        } catch (IOException e) {
          throw new RuntimeException("文件上传失败！", e);
        } finally {
          if (fos != null) {
            try {
              fos.close();
            } catch (IOException e) {
              throw new RuntimeException("文件上传->输出流关闭失败！！！！", e);
            }
          }
        }
      }
      return filePath;
    }
    
    public static String toJSONString(String key ,List<Object> value, ErrorCode code){
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(key, value);
      jsonObject.put("return", code);
      return jsonObject.toJSONString(); 
    }
    
}
