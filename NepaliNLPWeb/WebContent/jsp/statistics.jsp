<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page import="com.icodejava.research.nlp.services.ReportingService"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Admin Statistics</title>
<link href="../css/jquery-ui.css" rel="stylesheet">
<link href="../css/bootstrap.css" rel="stylesheet">
<link href="../css/nlpweb.css" rel="stylesheet">
<style>
body {
	width:650px;
	font-family: "Trebuchet MS", sans-serif;
	margin: 50px;
}
div.class1 {
    width:600px;
    margin: 10px;
    border: 3px solid #73AD21;
}
div.right {
	width:350x;
	align:right;
}
</style>
</head>
<body>
Admin > Statistics

<div class="ui-state-highlight-custom">
<%=ReportingService.getHTMLReport() %>
</div>
	<script src="../js/jquery.js"></script>
	<script src="../js/jquery-ui.js"></script>
	<script src="../js/bootstrap.js"></script>
		<script>
		$("[id*=controlgroup]").controlgroup();
	</script>
</body>
</html>