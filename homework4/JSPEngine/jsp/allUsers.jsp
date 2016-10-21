<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">

    <title>全部帐号</title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" type="text/css" href="css/bg.css">


  </head>

  <body>
    <hr>
    	<a href="index.jsp">返回主页</a>|<a href="">退出登录</a>
    <hr>
	<h1 align="center"><font color="#400040"><strong>全部帐号</strong></font></h1>

<c:if test="${!empty users.datas}">
	<table width="60%" border="0">
		<tbody>
			<th>编号</th>
			<th>账号</th>
			<th colspan="2">操作</th>
			<c:forEach items="${users.datas}" var="user">
				<tr>
					<td>${user.id}</td>
					<td>${user.username}</td>
					<td><a href="userAction.do?method=readUserInfo&id=${user.id }">查看</a></td>
					<td><a href="userAction.do?method=deleteUser&id=${user.id }">删除</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<TABLE width="778" border=0 align=center cellPadding=0 cellSpacing=0 borderColor=#ffffff style="FONT-SIZE: 10pt">
        <TBODY>
          <TR>
            <TD height=28 align=right vAlign=center nowrap="nowrap">&nbsp;&nbsp;
				<pg:pager url="userAction.do" items="${users.total }" export="currentPageNumber=pageNumber" maxPageItems="10">
					<pg:param name="method" value="allUsers"/>
					<pg:first>
						<a href="${pageUrl}">首页</a>
					</pg:first>
					<pg:prev>
						<a href="${pageUrl}">前页</a>
					</pg:prev>
					<pg:pages>
						<c:choose>
							<c:when test="${currentPageNumber eq pageNumber }">
								<font color="red">${pageNumber }</font>
							</c:when>
							<c:otherwise>
								<a href="${pageUrl }">${pageNumber }</a>
							</c:otherwise>
						</c:choose>
					</pg:pages>
					<pg:next>
						<a href="${pageUrl }">后页</a>
					</pg:next>
					<pg:last>
						<a href="${pageUrl }">尾页</a>
					</pg:last>
				</pg:pager>
    		</TD>
          </TR>
        </TBODY>
      </TABLE>
</c:if>

<c:if test="${empty users.datas}">
     <div align="center">
          没有任何记录
     </div>
</c:if>
</body>
</html>
