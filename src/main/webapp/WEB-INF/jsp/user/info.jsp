<%@ page language="java" pageEncoding="UTF-8"%>
<% 
String path = request.getContextPath(); 
// 获得项目完全路径（假设你的项目叫MyApp，那么获得到的地址就是 http://localhost:8080/MyApp/）: 
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path + "/"; 
%>

当前登录的用户为${currUser}  
<br/>  
<br/>  
<a href="<%=basePath%>user/loginUser/logout" target="_blank">Logout</a>