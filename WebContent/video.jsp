<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<html>
<head>
<script type="text/javascript" src="scripts/jwplayer.js"></script>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Play Video</title>

</head>

<body>
	<%@include file="header.jsp" %>
	<br/><br/><br/><br/>
	<h3>JWPlayer via CloudFront Stream Distribution<h3/>
	<div id="mediaplayer">Loading the player ...</div>
	<script type="text/javascript">
		jwplayer('mediaplayer')
				.setup(
						{
							file : "rtmp://<%=(String) session.getAttribute("streaming_distribution_domain_name") %>/cfx/st/<%=(String) session.getAttribute("video_file_name") %>",
							width : "720",
							height : "480"
						});
	</script>
	
	<br/><br/>
	<h3>HTML5 via CloudFront Download Distribution<h3/>
	<video width="720" height="480" controls poster="images/blue-video-icon.jpg">
    	<source src="http://<%=(String) session.getAttribute("download_distribution_domain_name") %>/<%=(String) session.getAttribute("video_file_name") %>" type="video/mp4"/>
	</video>
</body>
</html>
