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
    
    <title>My JSP 'switch.jsp' starting page</title>
    
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
    <font color="blue"><%=user.getZwmc() %></font><a href="selfInfo.jsp"><%=user.getUsername()%></a>登录中,<a href="">退出登录</a>
  <hr>
  <h2 align="center">合同管理系统</h2>
<table width="80%" border="0">
<% 
	if(user.getType()==1) {
%>
  <tr>
    <td><a href="userAction.do?method=allUsers">列出所有用户</a></td>
  </tr>
  <tr>
    <td><a href="create_user.html">创建新用户</a></td>
  </tr>
<% 
	} 
%>
  <tr>
    <td><a href="hetongAction.do?method=listAllHetong">列出所有合同</a></td>
  </tr>
<% 
	 if(user.getCreateable()==1) {
%>
  <tr>
    <td><a href="create_hetong.html">创建新合同</a></td>
  </tr>
<% 
	} 
%>
  <tr>
    <td><a href="selfInfo.jsp">个人资料</a></td>
  </tr>
</table>
</body>
</html>

