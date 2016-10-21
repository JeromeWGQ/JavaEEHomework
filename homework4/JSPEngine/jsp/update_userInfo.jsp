<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<%@ page import="com.jebysun.contractmanager.form.*"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'userInfo.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" type="text/css" href="css/bg.css">

  </head>
  
  <body>
<%
	UserForm user = (UserForm)session.getAttribute("user");
%>
    <hr>
    	<a href="index.jsp">返回主页</a>
    <hr>
   	<h2 align="center">修改资料</h2>
   	<form action="userAction.do?method=updateUserZl&id=<%=user.getId() %>" method="post">
     <table>
     	<tr>
     		<td>帐号:</td>
     		<td>
     			<input type="text" value="<%=user.getUsername() %>" name="username">
     		</td>
     	</tr>
     	<tr>
     		<td>密码:</td>
     		<td>
     			<input type="password" value="<%=user.getPassword() %>" name="password">
     		</td>
     	</tr>
     	<tr>
     		<td>确认密码:</td>
     		<td>
     			<input type="password" name="password2">
     		</td>
     	</tr>
     	<tr>
     		<td colspan="2">
     			<input type="reset" value="清空">
     			<input type="submit" value="确认">
     		</td>
     	</tr>
     </table>
    </form>
  </body>
</html>
