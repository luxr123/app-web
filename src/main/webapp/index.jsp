<%@ page language="java" pageEncoding="GBK" contentType="text/html;charset=gbk" isELIgnored="false"%> 
<% 
String path = request.getContextPath(); 
// 获得项目完全路径（假设你的项目叫MyApp，那么获得到的地址就是 http://localhost:8080/MyApp/）: 
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"; 
%> 
<html>
<head> 
   <!-- base需要放到head中 --> 
   <base href="<%=basePath%>"> 
</head> 
<body>
<h2>Hello World!</h2>
<a href="test/login.jsp">Login</a>
<%= request.getContextPath() %>
<%= basePath %>
</body>
</html>
