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
    
    <title>My JSP 'update_hetong.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" type="text/css" href="css/bg.css">

  </head>
  <% 
  		HeTongForm hetong = (HeTongForm)session.getAttribute("info");
  %>
  <body>
	<hr/>
	<a href="index.jsp">返回主页</a>
	<hr/>
	<h2 align="center">修改合同</h2>
	<form action="hetongAction.do?method=updateHeTong&htbh=<%=hetong.getHtbh() %>" method="post">
	  <table width="869" border="0" align="center">
        <tr>
          <td width="127">合同名称：</td>
          <td width="579">
            <input name="htmc" type="text" id="htmc" value="<%=hetong.getHtmc() %>" />
          </td>
          <td width="149">&nbsp;</td>
        </tr>
        <tr>
          <td>项目名称：</td>
          <td><input name="xmmc" type="text" id="xmmc" value="<%=hetong.getXmmc() %>"/></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>甲方：</td>
          <td><input name="jf" type="text" id="jf" value="<%=hetong.getJf() %>" /></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>甲方代理人：</td>
          <td><input name="jfdlr" type="text" id="jfdlr" value="<%=hetong.getJfdlr() %>" /></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>乙方：</td>
          <td><input name="yf" type="text" id="yf" value="<%=hetong.getYf() %>" /></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>乙方代理人：</td>
          <td><input name="yfdlr" type="text" id="yfdlr" value="<%=hetong.getYfdlr() %>" /></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>签署地点：</td>
          <td><input name="qsdd" type="text" id="qsdd" value="<%=hetong.getQsdd() %>"/></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>甲方签字日期：</td>
          <td><input name="jfqzrq" type="text" id="jfqzrq" value="<%=hetong.getJfqzrq() %>"/></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>乙方签字日期：</td>
          <td><input name="yfqzrq" type="text" id="yfqzrq" value="<%=hetong.getYfqzrq() %>"/></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>合同生效日期：</td>
          <td><input name="sxrq" type="text" id="sxrq" value="<%=hetong.getSxrq() %>"/></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>合同内容：</td>
          <td><textarea name="htnr" cols="80" rows="30" id="htnr"><%=hetong.getHtnr() %></textarea></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td align="center">
              <input type="reset" name="Submit" value="重置" />
              <input type="submit" name="Submit2" value="提交" />
        	</td>
          <td>&nbsp;</td>
        </tr>
      </table>
	</form>
</body>
</html>
