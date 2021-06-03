<%-- 
    Document   : selectwrite
    Created on : 2021. 5. 10., 오후 4:17:30
    Author     : 김태준
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.CommandType" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>선택</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:include page="header.jsp" />
        
        <div id="sidebar">
            <jsp:include page="sidebar_previous_menu.jsp" />
            <input type="button" value="타인에게 메일 쓰기"
               onclick="location.href='write_mail.jsp'">
            <input type="button" value="자신에게 메일 쓰기"
               onclick="location.href='write_mail_me.jsp'">
        </div>
    </body>
</html>
