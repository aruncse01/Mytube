
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD
 HTML 4.01 Transitional//EN"
 "http://www.w3.org/TR/html4/loose.dtd">

<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script>
	function validate() {
		var username = document.form.user.value;
		var password = document.form.user.value;
		if (username = "") {
			alert("Enter Username!!!!");
			return false;
		}
		if (password = "") {
			alert("Enter Password!!!!");
			return false;
		}
		return true;
	}
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>LOGIN</title>
</head>
<body>
<%@include file="header.jsp"%>
<br/><br/><br/>
	<form name="form" method="post" action="login">
		Username:<input type="text" name="username" /> <br/> Password:<input
			type="password" name="pass"><br/> <input type="submit"
			value="Submit">

	</form>
	
	<%@include file="footer.jsp" %>
</body>
</html>