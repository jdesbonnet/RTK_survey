<%@ page isErrorPage="true" import="java.io.*" contentType="text/html"%>
<!DOCTYPE html>
<!-- saved from url=(0053)https://getbootstrap.com/docs/4.3/examples/dashboard/ -->
<html lang="en">

	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<title>RTKSurvey Error</title>
	<link href="tirtbx.css" rel="stylesheet">

	<!-- Bootstrap core CSS -->
	<link href="./webjars/bootstrap/4.3.0/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">

	<link href="<%=context.get("FONT_AWESOME_ROOT")%>/css/font-awesome.min.css" rel="stylesheet">



	<style>
	.bd-placeholder-img {
		font-size: 1.125rem;
		text-anchor: middle;
		-webkit-user-select: none;
		-moz-user-select: none;
		-ms-user-select: none;
		user-select: none;
	}

	@media (min-width: 768px) {
		.bd-placeholder-img-lg {
			font-size: 3.5rem;
		}
	}
	</style>
	
	<!-- Custom styles for this template -->
	<link href="dashboard.css" rel="stylesheet">
	<style type="text/css">/* Chart.js */
		@-webkit-keyframes chartjs-render-animation{from{opacity:0.99}to{opacity:1}}@keyframes chartjs-render-animation{from{opacity:0.99}to{opacity:1}}.chartjs-render-monitor{-webkit-animation:chartjs-render-animation 0.001s;animation:chartjs-render-animation 0.001s;}
	</style>

	<script src="${JQUERY_SCRIPT}" crossorigin="anonymous"></script>
	
</head>


<body>
	<nav class="navbar navbar-dark fixed-top bg-dark flex-md-nowrap p-0 shadow">
		<a class="navbar-brand col-sm-3 col-md-2 mr-0" href="${contextPath}">TIRTBX v<%=context.get("VERSION")%></a>
		<input class="form-control form-control-dark w-100" type="text" placeholder="Search" aria-label="Search">
		<ul class="navbar-nav px-3">
			<li class="nav-item text-nowrap">
				<a class="nav-link" href="logout.jsp">Sign out</a>
			</li>
		</ul>
	</nav>

<div class="container-fluid">
	<div class="row">
		<nav class="col-md-2 d-none d-md-block bg-light sidebar">
			<div class="sidebar-sticky">
			</div>
		</nav>

		<main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-4"><div class="chartjs-size-monitor" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px; overflow: hidden; pointer-events: none; visibility: hidden; z-index: -1;"><div class="chartjs-size-monitor-expand" style="position:absolute;left:0;top:0;right:0;bottom:0;overflow:hidden;pointer-events:none;visibility:hidden;z-index:-1;"><div style="position:absolute;width:1000000px;height:1000000px;left:0;top:0"></div></div><div class="chartjs-size-monitor-shrink" style="position:absolute;left:0;top:0;right:0;bottom:0;overflow:hidden;pointer-events:none;visibility:hidden;z-index:-1;"><div style="position:absolute;width:200%;height:200%;left:0; top:0"></div></div></div>

	<br />
	
	<div class="alert alert-danger"><b class="fa fa-exclamation-triangle"></b> <%=exception.getMessage()%></div>

<pre>
StackTrace:
<%
	StringWriter stringWriter = new StringWriter();
	PrintWriter printWriter = new PrintWriter(stringWriter);
	exception.printStackTrace(printWriter);
	out.println(stringWriter);
	printWriter.close();
	stringWriter.close();
%>
</pre>

		</main>
	</div><!-- end .row -->
</div><!-- end .container-fluid -->

<script src="./webjars/bootstrap/4.3.0/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script>
var API = "${API}";
</script>
</body>
</html>