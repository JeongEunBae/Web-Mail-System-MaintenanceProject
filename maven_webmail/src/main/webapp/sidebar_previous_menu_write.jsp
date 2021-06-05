<%-- 
    Document   : sidebar_previous_menu_write
    Created on : 2021. 5. 11., 오후 2:59:57
    Author     : 김태준
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
        
        <a href="selectwrite.jsp"> 이전 메뉴로 </a>
    </body>
</html>
