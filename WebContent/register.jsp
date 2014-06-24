<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Register</title>

</head>
<body>
<%@include file="header.jsp"%>
	<form name="userform" method="post" action="register">
		<table>
			<tr>
				<td>User Name</td>
				<td><input type="text" name="user"></td>
			</tr>
			<tr>
				<td>Password</td>
				<td><input type="password" name="pass"></td>
			</tr>
			<tr>
				<td><input type="submit" value="Submit"></td>
			</tr>
		</table>
	</form>
	<%@include file="footer.jsp" %>
</body>
</html>