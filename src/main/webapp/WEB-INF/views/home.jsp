<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>병원 예약 시스템</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 40px 0;
            text-align: center;
        }

        h1 {
            margin-bottom: 20px;
            color: #333;
        }

        .container {
            width: 90%;
            max-width: 600px;
            margin: 0 auto;
        }

        .form-card {
            background-color: #fff;
            padding: 25px 30px;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
            margin-bottom: 30px;
        }
         
         .message-box {
            background-color: #d1e7dd;
            color: #0f5132;
            border: 1px solid #badbcc;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 30px;
        }

        .form-card h3 {
            margin-bottom: 20px;
            color: #2d89ef;
        }

        label {
            display: block;
            text-align: left;
            margin: 10px 0 5px;
            font-weight: bold;
        }

        input[type="text"],
        input[type="date"],
        select,
        textarea {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 6px;
            border: 1px solid #ccc;
            box-sizing: border-box;
        }

        textarea {
            height: 100px;
            resize: vertical;
        }

        input[type="submit"] {
            background-color: #2d89ef;
            color: white;
            border: none;
            padding: 10px 20px;
            font-weight: bold;
            border-radius: 5px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #1a5fc8;
        }

        .description {
            font-size: 14px;
            color: #777;
            margin-top: -10px;
            margin-bottom: 10px;
        }
    </style>
</head>
<%@ page import="java.time.LocalDate" %>
<%
    String today = LocalDate.now().toString(); // "yyyy-MM-dd" 형식
    request.setAttribute("today", today);
%>

<body>
    <div class="container">
        <div class="form-card">
            <h3>병원 사용자 로그인</h3>
            <p class="description">병원 측에서 예약 정보를 확인하려면 로그인하세요</p>
            <form action="${pageContext.request.contextPath}/hospital-login" method="get">
                <input type="submit" value="병원 사용자 로그인">
            </form>
        </div>

        <div class="form-card">
            <h3>키 생성</h3>
            <form action="${pageContext.request.contextPath}/generate-keys" method="get">
                <input type="submit" value="키 생성하기">
            </form>
            <c:if test="${not empty message}">
            	<div class="message-box">${message}</div>
        	</c:if>
        </div>

        <div class="form-card">
            <h3>병원 예약 신청</h3>
            <form action="${pageContext.request.contextPath}/reserve" method="post">
                <label>이름</label>
                <input type="text" name="name" required>

                <label>생년월일</label>
                <input type="text" name="birth" placeholder="예: 011210" required>

                <label>주민등록번호</label>
                <input type="text" name="ssn" placeholder="예: 1234567" required>

                <label>진료과</label>
                <select name="department" required>
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
                </select>

                <label>증상</label>
                <textarea name="symptom" required></textarea>

                <label>예약일자</label>
                <input type="date" name="date" min="${today}" required>


                <input type="submit" value="확인">
            </form>
        </div>
    </div>
</body>
</html>