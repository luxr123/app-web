<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>List All</title>
</head>
<body>
	<c:if test="${not empty user}">
		user:${user.username }<br>
		email:${user.email }<br>
	</c:if>
	<c:forEach var="user" items="${users }" varStatus="status" >
		${user.username}----${user.password}----${user.email}  
        <a href="<%=request.getContextPath()%>/user/${user.id}">查看</a>  
        <a href="<%=request.getContextPath()%>/user/${user.id}/update">编辑</a>  
        <a href="<%=request.getContextPath()%>/user/${user.id}/delete">删除</a>  
    <br/>  
	</c:forEach>
	<br/>  
	<a href="<%=request.getContextPath()%>/user/register">注册新用户</a>
</body>
</html>