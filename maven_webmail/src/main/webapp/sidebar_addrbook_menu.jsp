<%-- 
    Document   : sidebar_addrbook_menu
    Created on : 2021. 5. 11., 오후 5:51:48
    Author     : JIUK
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page errorPage="error_page.jsp"%>
<%@page import="cse.maven_webmail.control.CommandType" %>

<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>주소록 메뉴</title>
    </head>
    <body>
        <br> <br>

        <span style="color: indigo"> <strong>사용자: <%= session.getAttribute("userid")%> </strong> </span> <br>

        <p> <a href="main_menu.jsp"> 메인 화면 </a> </p>
        <p><a href="addrbook_form.jsp">주소록 추가</a></p>
        <p><a href="Login.do?menu=<%= CommandType.LOGOUT%>">로그아웃</a></p>
    </body>
</html>
