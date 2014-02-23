<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<% 
String path = request.getContextPath(); 
// 获得项目完全路径（假设你的项目叫MyApp，那么获得到的地址就是 http://localhost:8080/MyApp/）: 
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path + "/"; 
%>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>注册</title>
	<!-- base需要放到head中 -->
    <%-- <base href="<%=basePath%>"> --%>
	<script type="text/javascript" src="js/jquery-easyui-1.3.1/jquery-1.8.0.min.js"></script>
</head>
<body>

	<spring:bind path="command.*">
        <font color="#FF0000">
           <c:forEach items="${status.errorMessages}" var="error">
               	 错误: <c:out value="${error}"/><br>
           </c:forEach>
        </font>
    </spring:bind>
    
    <form action="<%=request.getContextPath() %>/register.mvc" method="post">
     <spring:bind path="command.name">
      name: <input type="text" name="name" value="<c:out value="${status.value}"/>"/>(必须输入)
       <c:if test="${status.error}">
          <font color="#FF0000">
        	  错误:
           <c:forEach items="${status.errorMessages}" var="error">
                <c:out value="${error}"/>
           </c:forEach>
          </font>
        </c:if>
     </spring:bind></br>
     <spring:bind path="command.sex">
     sex:  <input type="text" name="sex"/>(必须输入,且为0或1)
     <c:if test="${status.error}">
          <font color="#FF0000">
         	 错误:
           <c:forEach items="${status.errorMessages}" var="error">
                <c:out value="${error}"/>
           </c:forEach>
          </font>
        </c:if>
     </spring:bind></br>
           <input type="submit" value="submit"/>
     
   </form>

</body>
</html>