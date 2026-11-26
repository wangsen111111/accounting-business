<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>系统登录 - 高效排砂采气作业管理平台</title>
<%--    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>--%>
</head>
<body class="login_bg">
<section class="loginBox">
    <header class="loginHeader">
<%--        <h1></h1>--%>
        <head lang="zh-CN">
            <meta charset="UTF-8">
            <title>系统登录 - 高效排砂采气作业管理平台</title>

            <!-- 内联美食风格 CSS -->
            <style>
                body.login_bg {
                    background-color: #f5f7fa; /* 浅灰色背景，干净清爽 */
                    font-family: "Segoe UI", Arial, sans-serif;
                    margin: 0;
                    padding: 0;
                }

                .loginBox {
                    width: 400px;
                    margin: 100px auto;
                    background: #ffffff;
                    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
                    border-radius: 8px;
                    overflow: hidden;
                    border: 1px solid #d9dfe5;
                }

                .loginHeader {
                    background-color: #2c3e50; /* 深蓝色头部 */
                    padding: 20px;
                    text-align: center;
                    color: white;
                    font-size: 20px;
                    font-weight: bold;
                    border-bottom: 1px solid #1a252f;
                }

                .loginCont {
                    padding: 30px 30px;
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
                    margin-bottom: 6px;
                    color: #34495e;
                    font-weight: 600;
                }

                .inputbox input[type="text"],
                .inputbox input[type="password"] {
                    width: 100%;
                    padding: 10px 12px;
                    border: 1px solid #bdc3c7;
                    border-radius: 4px;
                    font-size: 14px;
                    box-sizing: border-box;
                    transition: border-color 0.3s;
                }

                .inputbox input:focus {
                    border-color: #3498db;
                    outline: none;
                }

                .subBtn {
                    text-align: center;
                    margin-top: 10px;
                }

                .subBtn input[type="submit"],
                .subBtn input[type="reset"] {
                    width: 100px;
                    padding: 10px 0;
                    margin: 0 8px;
                    font-size: 15px;
                    font-weight: bold;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                    color: white;
                    transition: background 0.3s;
                }

                .subBtn input[type="submit"] {
                    background-color: #3498db; /* 蓝色 - 登录按钮 */
                    box-shadow: 0 3px 6px rgba(52, 152, 219, 0.3);
                }

                .subBtn input[type="reset"] {
                    background-color: #95a5a6; /* 灰色 - 重置按钮 */
                    box-shadow: 0 3px 6px rgba(149, 165, 166, 0.3);
                }

                .subBtn input:hover {
                    opacity: 0.9;
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
