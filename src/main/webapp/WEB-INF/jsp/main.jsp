<%@ page language="java" pageEncoding="UTF-8"%>
<% 
String path = request.getContextPath(); 
// 获得项目完全路径（假设你的项目叫MyApp，那么获得到的地址就是 http://localhost:8080/MyApp/）: 
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path + "/"; 
%>

普通用户可访问
<a href="<%=basePath%>user/loginUser/getUserInfo"
	target="_blank">用户信息页面</a>
<br />
<br />
管理员可访问
<a href="<%=basePath%>admin/listUser.jsp"
	target="_blank">用户列表页面</a>
<br />
<br />
<a href="<%=basePath%>user/loginUser/logout" target="_blank">Logout</a>