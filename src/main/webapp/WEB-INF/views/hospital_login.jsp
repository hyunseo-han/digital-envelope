<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<title>병원 로그인</title>
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

.login-container {
	background-color: #ffffff;
	padding: 40px 30px;
	border-radius: 12px;
	box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
	width: 100%;
	max-width: 400px;
	text-align: center;
}

.login-container h2 {
	color: #2d89ef;
	margin-bottom: 20px;
}

label {
	display: block;
	margin-bottom: 5px;
	font-weight: bold;
	text-align: left;
}

input[type="text"], input[type="password"] {
	width: 100%;
	padding: 10px;
	margin-bottom: 20px;
	border-radius: 6px;
	border: 1px solid #ccc;
	box-sizing: border-box;
}

input[type="submit"] {
	background-color: #2d89ef;
	color: white;
	border: none;
	padding: 10px 20px;
	font-weight: bold;
	border-radius: 5px;
	cursor: pointer;
	width: 100%;
}

input[type="submit"]:hover {
	background-color: #1a5fc8;
}

.error-message {
	background-color: #f8d7da;
	color: #842029;
	border: 1px solid #f5c2c7;
	padding: 10px;
	border-radius: 6px;
	margin-bottom: 20px;
}
</style>
</head>
<body>
	<div class="login-container">
		<h2>병원 사용자 로그인</h2>


		<form action="${pageContext.request.contextPath}/hospital-login"
			method="post">
			<label for="username">ID</label> <input type="text" id="username"
				name="username" required> <label for="password">PW</label> <input
				type="password" id="password" name="password" required> <label>
				<input type="checkbox" name="rememberme"> 로그인 상태 유지
			</label> <br />
			<input type="submit" value="로그인">
		</form>

	</div>
</body>
</html>
