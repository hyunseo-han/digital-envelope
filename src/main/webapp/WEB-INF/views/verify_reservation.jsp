<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>예약 검증 결과</title></head>
<body>
    <h2>병원 검증 결과</h2>
    <p>${result}</p>
    <h3>예약 내용</h3>
    <p>${originalData}</p>

    <form method="post" action="${pageContext.request.contextPath}/verify-reservation">
        <button type="submit">복호화 및 전자서명 검증</button>
    </form>
</body>
</html>
