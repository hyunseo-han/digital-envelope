<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>병원 로그인</title>
</head>
<body>
    <h2>병원 사용자 로그인</h2>
    <form action="${pageContext.request.contextPath}/hospital-login" method="post">
        ID: <input type="text" name="username"><br>
        PW: <input type="password" name="password"><br>
        <input type="submit" value="로그인">
    </form>
</body>
</html>
