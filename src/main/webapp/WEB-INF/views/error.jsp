<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>오류 발생</title>
<style>
body {
	font-family: "Segoe UI", sans-serif;
	background-color: #fff3f3;
	padding: 30px;
}

.container {
	background: white;
	border-radius: 8px;
	padding: 24px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
	max-width: 500px;
	margin: auto;
	border-left: 6px solid #dc3545;
}

h2 {
	color: #dc3545;
}

.btn-home {
	display: inline-block;
	margin-top: 20px;
	padding: 10px 16px;
	background-color: #6c757d;
	color: white;
	text-decoration: none;
	border-radius: 6px;
}

.btn-home:hover {
	background-color: #495057;
}
</style>
</head>
<body>

	<div class="container">
		<h2>🚨 오류가 발생했습니다</h2>
		<p>${error}</p>
		<a href="${pageContext.request.contextPath}" class="btn-home">홈으로
			돌아가기</a>
	</div>

</body>
</html>
