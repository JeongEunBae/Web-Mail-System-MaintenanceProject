<%-- 
    Document   : trash_mail
    Created on : 2021. 5. 19., 오후 10:04:02
    Author     : chung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page errorPage="error_page.jsp"%>

<!DOCTYPE html>

<jsp:useBean id="pop3" scope="page" class="cse.maven_webmail.model.Pop3Agent" />
<%
            pop3.setHost((String) session.getAttribute("host"));
            pop3.setUserid((String) session.getAttribute("userid"));
            pop3.setPassword((String) session.getAttribute("password"));
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>임시 메일함</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:include page="header.jsp" />
        
        <div id="sidebar">
            <span style="color: indigo"> <strong>사용자: <%= session.getAttribute("userid") %> </strong> </span> <br>
            
            <p> <a href="main_menu.jsp"> 메인 화면 </a> </p>
        </div>

        <div id="main">
            <%= pop3.getTrashMessageList() %>
        </div>
        
            <jsp:include page="footer.jsp" />
    </body>
</html>
