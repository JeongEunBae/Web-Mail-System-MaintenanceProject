<%-- 
    Document   : error_page
    Created on : 2021. 6. 5., 오전 10:17:32
    Author     : chung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isErrorPage="true"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>웹메일 시스템 오류 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <%@include file="header.jspf"%>
        
        <p id="login_fail">
            예상하지 못한 오류가 발생했습니다. <br/><br/>
            오류의 원인은 <%= exception.toString() %>입니다. <br/>
            
            <a href="<%= getServletContext().getInitParameter("HomeDirectory") %>" title="초기화면">초기화면으로 돌아가기<br/>
        </p>
        
        <%@include file="footer.jspf"%>
    </body>
</html>
