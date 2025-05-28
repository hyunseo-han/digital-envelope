<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>JSP 홈</title>
</head>
<body>
	<h3>병원 사용자</h3>
	<form action="${pageContext.request.contextPath}/hospital-login" method="get">
	    <input type="submit" value="병원 사용자 로그인">
	</form>
	
	<form action="${pageContext.request.contextPath}/generate-keys" method="get">
	    <input type="submit" value="키 생성하기">
	</form>

    <form action="${pageContext.request.contextPath}/reserve" method="post">
        이름: <input type="text" name="name"><br>
        생년월일: <input type="text" name="birth"><br>
        진료과: <input type="text" name="department"><br>
        증상: <textarea name="symptom"></textarea><br>
        예약일자: <input type="date" name="date"><br>
        <input type="submit" value="예약 요청">
    </form>
</body>
</html>
