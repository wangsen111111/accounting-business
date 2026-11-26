<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>系统登录 - 企业车辆管理系统</title>
<%--    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>--%>
</head>
<body class="login_bg">
<section class="loginBox">
    <header class="loginHeader">
<%--        <h1>企业车辆管理系统</h1>--%>
        <head lang="zh-CN">
            <meta charset="UTF-8">
            <title>系统登录 - 企业车辆管理系统</title>

            <!-- 内联美食风格 CSS -->
            <style>
                body.login_bg {
                    background-color: #fff8f0;
                    font-family: "Microsoft YaHei", sans-serif;
                    margin: 0;
                    padding: 0;
                }

                .loginBox {
                    width: 420px;
                    margin: 100px auto;
                    background: #fffefb;
                    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
                    border-radius: 12px;
                    overflow: hidden;
                    border: 1px solid #f5deb3;
                }

                .loginHeader {
                    background: linear-gradient(to right, #ffb347, #ffcc33);
                    padding: 25px;
                    text-align: center;
                    color: white;
                    font-size: 22px;
                    font-weight: bold;
                    border-bottom: 1px solid #f3d29d;
                }

                .loginCont {
                    padding: 30px 40px;
                }

                .info {
                    color: #e74c3c;
                    font-size: 14px;
                    margin-bottom: 15px;
                    text-align: center;
                }

                .inputbox {
                    margin-bottom: 20px;
                }

                .inputbox label {
                    display: block;
                    margin-bottom: 8px;
                    color: #8b4513;
                    font-weight: bold;
                }

                .inputbox input[type="text"],
                .inputbox input[type="password"] {
                    width: 100%;
                    padding: 10px 12px;
                    border: 1px solid #d7ccc8;
                    border-radius: 6px;
                    font-size: 15px;
                    box-sizing: border-box;
                    transition: border-color 0.3s;
                }

                .inputbox input:focus {
                    border-color: #ffa726;
                    outline: none;
                }

                .subBtn {
                    text-align: center;
                }

                .subBtn input[type="submit"],
                .subBtn input[type="reset"] {
                    width: 120px;
                    padding: 10px 0;
                    margin: 0 10px;
                    font-size: 16px;
                    font-weight: bold;
                    border: none;
                    border-radius: 25px;
                    cursor: pointer;
                    transition: background 0.3s, transform 0.2s;
                    color: white;
                }

                .subBtn input[type="submit"] {
                    background: linear-gradient(to right, #ff7e5f, #feb47b);
                    box-shadow: 0 4px 10px rgba(255, 126, 95, 0.4);
                }

                .subBtn input[type="reset"] {
                    background: linear-gradient(to right, #ffe082, #ffca28);
                    box-shadow: 0 4px 10px rgba(255, 202, 40, 0.4);
                    color: #5d4037;
                }

                .subBtn input:hover {
                    transform: scale(1.05);
                }
            </style>
        </head>

    </header>
    <section class="loginCont">
        <form class="loginForm" action="${pageContext.request.contextPath}/login.do" method="post" name="actionForm"
              id="actionForm">
            <div class="info">${error}</div>
            <div class="inputbox">
                <label for="userCode">用户名：</label>
                <input type="text" class="input-text" id="userCode" name="userCode" placeholder="请输入用户名" required/>
            </div>
            <div class="inputbox">
                <label for="userPassword">密码：</label>
                <input type="password" id="userPassword" name="userPassword" placeholder="请输入密码" required/>
            </div>
            <div class="subBtn">
                <input type="submit" value="登录"/>
                <input type="reset" value="重置"/>
            </div>
        </form>
    </section>
</section>
</body>
</html>
