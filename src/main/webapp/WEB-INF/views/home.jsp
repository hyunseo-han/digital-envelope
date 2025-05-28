<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>JSP 홈</title>
</head>
<body>
    <h1>${message}</h1>

    <form action="reserve" method="post">
        이름: <input type="text" name="name"><br>
        생년월일: <input type="text" name="birth"><br>
        진료과: <input type="text" name="department"><br>
        증상: <textarea name="symptom"></textarea><br>
        예약일자: <input type="date" name="date"><br>
        <input type="submit" value="예약 요청">
    </form>
</body>
</html>
