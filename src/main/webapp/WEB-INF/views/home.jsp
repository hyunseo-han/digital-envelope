<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>JSP 홈</title>
</head>
<body>
	<h3>병원 사용자</h3>
	<form action="${pageContext.request.contextPath}/hospital-login"
		method="get">
		<input type="submit" value="병원 사용자 로그인">
	</form>

	<form action="${pageContext.request.contextPath}/generate-keys"
		method="get">
		<input type="submit" value="키 생성하기">
	</form>

	<form action="${pageContext.request.contextPath}/reserve" method="post">
		이름: <input type="text" name="name"><br> 
		생년월일: <input type="text" name="birth" placeholder="예: 011210"><br> 
		주민등록번호: <input type="text" name="ssn" placeholder="예: 1234567"><br> 
		진료과: <select name="department">
			<option value="">-- 진료과 선택 --</option>
			<option>가정의학과</option>
			<option>마취통증의학과</option>
			<option>성형외과</option>
			<option>신경외과</option>
			<option>안과</option>
			<option>정신건강의학과</option>
			<option>치과</option>
			<option>감염내과</option>
			<option>방사선종양학과</option>
			<option>소아청소년과</option>
			<option>신장내과</option>
			<option>외과</option>
			<option>정형외과</option>
			<option>피부과</option>
			<option>내분비대사내과</option>
			<option>비뇨의학과</option>
			<option>소화기내과</option>
			<option>심장혈관내과</option>
			<option>이비인후-두경부외과</option>
			<option>중앙혈액내과</option>
			<option>핵의학과</option>
			<option>류마티스내과</option>
			<option>산부인과</option>
			<option>신경과</option>
			<option>심장혈관흉부외과</option>
			<option>재활의학과</option>
			<option>진단검사의학과</option>
			<option>호흡기-알레르기내과</option>
		</select><br> 증상:
		<textarea name="symptom"></textarea>
		<br> 예약일자: <input type="date" name="date"><br> <input
			type="submit" value="예약 요청">
	</form>

</body>
</html>
