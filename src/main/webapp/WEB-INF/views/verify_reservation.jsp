<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <title>예약 검증</title>
</head>
<body>
    <h2>병원: 예약 검증 시스템</h2>

   <form action="${pageContext.request.contextPath}/verify-reservation" method="post">
    <label>전자봉투 (Base64):</label><br>
    <textarea name="envelope" rows="8" cols="80"></textarea><br>
    <label>AES 키 (병원 공개키로 암호화된 것, Base64):</label><br>
    <textarea name="encryptedKey" rows="4" cols="80"></textarea><br>
    <input type="submit" value="검증 실행">
</form>

<c:if test="${not empty result}">
    <p><strong>검증 결과:</strong> ${result}</p>
    <c:if test="${not empty originalData}">
        <p><strong>예약 정보:</strong><br>${originalData}</p>
    </c:if>
</c:if>

</body>
</html>
