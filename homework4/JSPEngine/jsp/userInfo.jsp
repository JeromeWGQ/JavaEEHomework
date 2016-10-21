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
    	UserForm user = (UserForm)session.getAttribute("userInfo");
    %>
    <hr>
    	<a href="index.jsp">返回主页</a>
    <hr>
    <h3 align="center">账号信息</h3>
     <table>
     	<tr>
     		<td>帐号:</td>
     		<td><%=user.getUsername() %></td>
     	</tr>
     	<tr>
     		<td>职位:</td>
     		<td><%=user.getZwmc() %></td>
     	</tr>
     	<tr>
     		<td>姓名:</td>
     		<td><%=user.getRealName() %></td>
     	</tr>
     	<tr>
     		<td>权限:</td>
     		<td>
     			<%=user.getReadable()==1?"查看":"" %>
     			<%=user.getUpdateable()==1?"修改":""%>
     			<%=user.getCreateable()==1?"创建":""%>
     			<%=user.getDeleteable()==1?"删除":""%>
     		</td>
     	</tr>
     	<tr>
     		<td>创建日期:</td>
     		<td><%=user.getCreatedate() %></td>
     	</tr>
     	<tr>
     		<td colspan="2"><a href="update_userQx.jsp">修改职位权限</a></td>
     	</tr>
     </table>
  </body>
</html>
