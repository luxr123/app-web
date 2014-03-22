<%@ page language="java" pageEncoding="UTF-8"%>  
<html>
<% 
String path = request.getContextPath(); 
// 获得项目完全路径（假设你的项目叫MyApp，那么获得到的地址就是 http://localhost:8080/MyApp/）: 
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path + "/"; 
%>
<head>
<script type="text/javascript">  
        //用于刷新验证码  
         function myReload(){
            document.getElementById("verifyCodeImage").src=document.getElementById("verifyCodeImage").src+"?nocache="+new Date().getTime();    
        }    
</script> 
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>www.dream.com 在线登录 </title>
</head>
<div style="color:red; font-size:22px;">${message_login}</div>  

<form action="<%=basePath%>user/loginUser/login" method="POST">
    姓名：<input type="text" name="username"/><br/>  
    密码：<input type="text" name="password"/><br/>  
    验证：<input type="text" name="verifyCode"/>  
         &nbsp;&nbsp;  
         <img border=0 src="<%=basePath%>image/captcha_image" width="55" height="25" id = "verifyCodeImage" style="margin-bottom: -5px" /><br/>  
		 <a href = "#" style = "font-size: 13px;margin-left: 5px;" onclick = "myReload()">换一张</a>
    <input type="submit" value="确认"/>  
</form>
</html>