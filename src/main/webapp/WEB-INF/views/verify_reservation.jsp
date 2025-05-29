<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>예약 검증</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .verify-container {
            background-color: #ffffff;
            padding: 40px 30px;
            border-radius: 12px;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 600px;
            text-align: center;
        }

        .verify-container h2 {
            color: #2d89ef;
            margin-bottom: 20px;
        }

        label {
            display: block;
            text-align: left;
            font-weight: bold;
            margin-bottom: 8px;
        }

        textarea {
            width: 100%;
            height: 180px;
            padding: 12px;
            border-radius: 6px;
            border: 1px solid #ccc;
            font-family: monospace;
            resize: vertical;
            margin-bottom: 20px;
            box-sizing: border-box;
        }

        input[type="submit"] {
            background-color: #2d89ef;
            color: white;
            border: none;
            padding: 12px 24px;
            font-weight: bold;
            border-radius: 6px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #1a5fc8;
        }

        .result-message {
            margin-top: 25px;
            background-color: #e2f0d9;
            color: #2d6a4f;
            border: 1px solid #b7dfb0;
            padding: 15px;
            border-radius: 8px;
        }
    </style>
</head>
<body>
    <div class="verify-container">
        <h2>최신 진료 예약</h2>
		<form action="${pageContext.request.contextPath}/verify-reservation" method="get">
		    <input type="submit" value="검증 실행">
		</form>


		<c:if test="${not empty result}">
		    <div class="result-message">
		        <strong>복호화 결과:</strong><br/>
		        <pre>${result}</pre>
		    </div>
		</c:if>


    </div>
</body>
</html>
