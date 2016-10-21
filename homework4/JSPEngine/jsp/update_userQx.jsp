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
   	<h2 align="center">修改账号权限职位</h2>
   	<form action="userAction.do?method=updateUserQx&id=<%=user.getId() %>" method="post">
     <table align="center">
     	<tr>
     		<td>职位:</td>
     		<td>
     			<input type="text" name="zwmc">
     		</td>
     	</tr>
     	<tr>
     		<td>读阅合同:</td>
     		<td><select name="readable" id="readable">
              <option value="0">无</option>
              <option value="1">有</option>
            </select>     		</td>
     	</tr>
     	<tr>
     		<td>修改合同:</td>
     		<td><select name="updateable" id="updateable">
              <option value="0">无</option>
              <option value="1">有</option>
            </select>     		</td>
     	</tr>
		<tr>
     		<td>创建合同:</td>
     		<td><select name="createable" id="createable">
              <option value="0">无</option>
              <option value="1">有</option>
            </select>     		</td>
     	</tr>
		<tr>
     		<td>删除合同:</td>
     		<td><select name="deleteable" id="deleteable">
              <option value="0">无</option>
              <option value="1">有</option>
            </select>     		</td>
     	</tr>
     	<tr>
     		<td colspan="2" align="center">
     			<input type="reset" value="清空">
     			<input type="submit" value="确认">
     		</td>
     	</tr>
     </table>
    </form>
  </body>
</html>
