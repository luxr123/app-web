<%@ page language="java" pageEncoding="GBK" contentType="text/html;charset=gbk" isELIgnored="false"%> 
<% 
String path = request.getContextPath(); 
// �����Ŀ��ȫ·�������������Ŀ��MyApp����ô��õ��ĵ�ַ���� http://localhost:8080/MyApp/��: 
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"; 
%> 
<html>
<head> 
   <!-- base��Ҫ�ŵ�head�� --> 
   <base href="<%=basePath%>"> 
</head> 
<body>
<h2>Hello World!</h2>
<a href="test/login.jsp">Login</a>
<%= request.getContextPath() %>
<%= basePath %>
</body>
</html>
