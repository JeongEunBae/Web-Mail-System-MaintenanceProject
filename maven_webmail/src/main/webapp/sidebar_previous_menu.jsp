<%-- 
    Document   : sidebar_adduser_menu
    Author     : jongmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page errorPage="error_page.jsp"%>

<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <br> <br> 

        <span style="color: indigo">
            <strong>사용자: <%= session.getAttribute("userid") %> </strong>
        </span> <br> <br>
        
        <p><a href="reply_mail.jsp"> 답장 쓰기 </a></p>
        <p><a href="main_menu.jsp"> 이전 메뉴로 </a></p>
    </body>
</html>
