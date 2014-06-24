<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<head>
<title>My Videos</title>
<link href="bootstrap/bootstrap/css/bootstrap.css" rel="stylesheet">
    <style type="text/css">
      body {
        padding-top: 20px;
        padding-bottom: 40px;
		background-color:#FCB826;
		
      }

      /* Custom container */
      .container-narrow {
        margin: 0 auto;
        max-width: 700px;
      }
      .container-narrow > hr {
        margin: 30px 0;
      }

      /* Main marketing message and sign up button */
      .jumbotron {
        margin: 60px 0;
        text-align: center;
      }
      .jumbotron h1 {
        font-size: 72px;
        line-height: 1;
      }
      .jumbotron .btn {
        font-size: 21px;
        padding: 14px 24px;
      }

      /* Supporting marketing content */
      .marketing {
        margin: 60px 0;
      }
      .marketing p + h4 {
        margin-top: 28px;
      }
    </style>
    <link href="../assets/css/bootstrap-responsive.css" rel="stylesheet">
    <link href="styles/jquery.rating.css" rel="stylesheet" type="text/css" />
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<script language="javascript" type="text/javascript"
	src="scripts/jquery-1.9.1.min.js"></script>
<script language="javascript" type="text/javascript"
	src="scripts/jquery.rating.pack.js"></script>
<script language="javascript" type="text/javascript"
	src="scripts/jquery.form.js"></script>
<script language="javascript" type="text/javascript"
	src="scripts/jquery.MetaData.js"></script>


	
	<script src="bootstrap/bootstrap/js/bootstrap.js"></script>
<script>

$(document).ready(function() { 

	<c:forEach items="${sessionScope.videoList}" var="item">
		//1. rating submition(this must come first,otherwise there will be no callback functionality)
		$('#video-list input.auto-submit-star').rating({
		  callback: function(value, link){
			  var form, fields, newField;
	 	 	  	form = $(this.form);

	 	 	  	$('<input>').attr({
	 	 	    	type: 'hidden',
	 	 	    	name: 'req-type',
	 	 	    	value: 'rate'
	 	 		}).appendTo(form);
		 	 	
				form.ajaxSubmit();
		  }
		 });
		//2. select rating 
		$('#${item.id} input.auto-submit-star').rating('select',${item.averageRating-1},false);

		//3. rating cancel submition
		$('#${item.id} div.rating-cancel').click(function() {
 	 		var form, fields, newField;
 	 	  	form = $('#${item.id}');

 	 	  	$('<input>').attr({
	 	    	type: 'hidden',
	 	    	name: 'req-type',
	 	    	value: 'rate'
	 		}).appendTo(form);
 	 	  	$('<input>').attr({
 	 	    	type: 'hidden',
 	 	    	name: 'rating',
 	 	    	value: '0'
 	 		}).appendTo(form);
	 	 	
			form.ajaxSubmit();
		});
		
		//deletion link
		$("#${item.id} a.removeRecord").click(function(){
   			event.stopPropagation();
   			if(confirm("Do you want to delete?")) {
   				var form, fields, newField;
	 	 	  	form = $("#${item.id}");

	 	 	  	$('<input>').attr({
	 	 	    	type: 'hidden',
	 	 	    	name: 'req-type',
	 	 	    	value: 'delete'
	 	 		}).appendTo(form);
		 	 	
				form.ajaxSubmit();
   			}
   			else
   			{
   				
   			}       
   			event.preventDefault();
   			window.location="list";
   
		});
	</c:forEach>
})

</script>

</head>

<body>
	<%@include file="header.jsp"%>
	<%/*
		String username = (String) session.getAttribute("username");
		if (username == null) {
			response.sendRedirect("index.jsp");
		}*/
	%>
	<div id="video-list">
		<c:forEach items="${sessionScope.videoList}" var="item">
			<ul>
				<li><a
					href="playVideo?stream_dist_id=${item.streamingDistributionId}&download_dist_id=${item.downLoadDistributionId}&video_file_name=${item.videoName}"><img
						class="video-thumbnail" src="images/blue-video-icon.jpg" /></a>
					<h3>${item.videoName}</h3>
					<form id="${item.id}" action="videoMgr">
						<p>
							<input
								type="hidden" name="video-id" value="${item.id}" /><input
								type="hidden" name="rating-count" value="${item.ratingCount}" /><input
								type="hidden" name="original-rating" value="${item.rating}" />
							<c:forEach var="i" begin="1" end="20" step="1" varStatus ="status">
								<input name="rating" type="radio" value="${i}"
								class="auto-submit-star {split:4}" /> 
							</c:forEach>
						</p>
						<p>${item.ratingCount} votes</p>
						<br>
						<p>Uploaded by ${item.userName} at ${item.timestamp}</p>
						<%
		String username = (String) session.getAttribute("username");
		if (username != null) {
			response.getWriter().print("<p><a class="+"removeRecord"+" href="+"#"+">Delete</a></p>");
		}
	%>
						
					</form></li>

			</ul>
		</c:forEach>

	</div>
	
	
	<%@include file="footer.jsp" %>
</body>
</html>