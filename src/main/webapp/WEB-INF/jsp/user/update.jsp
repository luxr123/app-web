<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Update</title>
</head>
<body>
<!-- 此时没有写action,直接提交会提交给/update -->
<form:form method="POST" modelAttribute="user">
    username: <form:input path="username"/>
    <form:errors path="username" cssStyle="color:red"></form:errors><br/>  
    password: <form:password path="password"/>
    <form:errors path="password" cssStyle="color:red"></form:errors><br/>  
    email: <form:input path="email"/>
    <form:errors path="email" cssStyle="color:red"></form:errors><br/>
    mobile: <form:input path="mobilePhoneNumber"/>
    <form:errors path="mobilePhoneNumber" cssStyle="color:red"></form:errors><br/>
    <input type="submit" value="更新用户信息"/>  
</form:form>
</body>
</html>