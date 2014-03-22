<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<% 
String path = request.getContextPath(); 
// 获得项目完全路径（假设你的项目叫MyApp，那么获得到的地址就是 http://localhost:8080/MyApp/）: 
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path + "/"; 
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>www.dream.com 在线注册 </title>
	<meta name="article" id="articlelink" content="/technology/jquerytutorial/20120905-register-with-password-strength/" />
	<link rel="stylesheet" href="<%=basePath%>css/style.css">
	
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	
	<script src="<%=basePath%>js/jquery.complexify.js"></script>
	
	<script src="<%=basePath%>js/jquery.placeholder.min.js"></script>
	
	<script src="<%=basePath%>js/raphael.2.1.0.min.js"></script>
	
	<script src="<%=basePath%>js/justgage.1.0.1.min.js"></script>

	<script>
		$(function(){
			$('#submit').attr('disabled', true);
			
			var g1 = new JustGage({
			  id: "complexity", 
			  value: 0, 
			  min: 0,
			  max: 100,
			  title: "密码强度提示",
			  titleFontColor: '#9d7540',
			  valueFontColor : '#CCCCCC',
			  label: "points",
				levelColors: [
				  "#dfa65a",
				  "#926d3b",
				  "#584224"
				]    
			});		
		
			$('input[placeholder]').placeholder();
			$("#password").complexify({}, function(valid, complexity){
				if(valid){
					$('#submit').fadeIn();
				}else{
					$('#submit').fadeOut();
				}				
				g1.refresh(Math.round(complexity));	
			});
			
			$('#submit').click(function(){
				$('#msgbox').html('welcome to gbtags.com');
			});
		});
	</script>
</head>
<body>
<form:form commandName="user" action="addUser" method="post">
<div id="container">
	<div id="page-wrap">
		<div id="title">注册新账号 - www.dream.com</div>
		<p>
		<input type="text" name="username" id="username" placeholder="用户名" value="${user.username}"/>
		<form:errors path="username" cssStyle="color:red"></form:errors>
		</p>
		<p>
		<input type="text" name="email" id="email" placeholder="电子邮件" value="${user.email}"/>
		<form:errors path="email" cssStyle="color:red"></form:errors>
		</p>
		<p>
		<input type="text" name="mobilePhoneNumber" id="mobilePhoneNumber" placeholder="手机号码" value="${user.mobilePhoneNumber}"/>
		<form:errors path="mobilePhoneNumber" cssStyle="color:red"></form:errors>
		</p>
		<p>
		<input type="password" name="password" id="password" placeholder="密码"/>
		<form:errors path="password" cssStyle="color:red"></form:errors>
		</p>
		<p>
		<input type="text" name="code" id="code" placeholder="验证码"/>
		<form:errors path="code" cssStyle="color:red"></form:errors>
		<img border=0 src="<%=basePath%>image/captcha_image" width="55" height="25" id = "imageMask" style="margin-bottom: -5px" />
		<a href = "#" style = "font-size: 13px;margin-left: 5px;" onclick = "myReload()">换一张</a>
		</p>
		<div id="complexity"></div>
		<p>
		<input type="reset" value="重填" />
		<input type="submit" value="注册" />
<!-- 		<input type="submit" name="submit" id="submit" value="注册" /> -->
		</p>
		<p id="msgbox"></p>
	</div>
</div>
</form:form>
<div id="gbin1">&copy;&nbsp;<a href="http://www.gbin1.com">www.dream.com </a></div>
</body>
<script type="text/javascript">  
        //用于刷新验证码  
         function myReload(){    
            document.getElementById("imageMask").src=document.getElementById("imageMask").src+"?nocache="+new Date().getTime();    
        }    
</script>
</html>