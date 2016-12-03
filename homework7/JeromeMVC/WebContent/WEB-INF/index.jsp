<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>这是index</title>
	</head>
	<body>
		<form action="hello" method="post">
			<table>
				<tr><td>用户名</td><td><input type="text" name="name"></td></tr>
				<tr><td>密码</td><td><input type="password" name="pas"></td></tr>
				<tr><td><input type="submit" value="提交"></td><td><input type="reset" value="取消"></td></tr>
			</table>
		</form>
	</body>
</html>