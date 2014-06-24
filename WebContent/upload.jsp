<%@page import="java.util.Iterator"%>
 <%@page
import="java.util.ArrayList"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="en-US" xmlns="http://www.w3.org/1999/xhtml" dir="ltr">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<head>
<title>Upload</title>
<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
<script language="javascript" type="text/javascript"
	src="scripts/jquery-1.9.1.min.js"></script>

<script type="text/javascript">

function type_checking (){
	var ext = $('#filename').val().split('.').pop().toLowerCase();
	if($.inArray(ext, ['mp4','m4v','f4v','mov']) == -1) {
		alert('unsupported file type!');
		return false;
	}
	return true;
}
$(document).ready(function() { 
	$('#filename').change(type_checking)
	$('#file_upload_form').submit(type_checking)
});
</script>
</head>
<body>
	<%@include file="header.jsp" %> <% String
	username=(String)session.getAttribute("username"); if(username==null) {
	 response.sendRedirect("login.jsp"); }
	%>


<br/>
<br/>
<br/>
	<fieldset>
        <legend>Upload File</legend>
        <form id="file_upload_form" action="upload" method="post" enctype="multipart/form-data">
            <label for="filename_1">File:[mp4, m4v, f4v, mov] </label><br/>
            <input id="filename" type="file" name="filename_1" size="50"/>
            <input type="submit" value="Upload File"/>
        </form>
    </fieldset>
    <%@include file="footer.jsp" %>
</body>
</html>

