<%-- 
    Document   : reset_password
    Created on : 2021. 5. 18., 오후 4:46:10
    Author     : chung
--%>

<%@page import="cse.maven_webmail.model.ResetPasswordAgent"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<script type="text/javascript">
    <!--
    function gohome(){
        window.location = "/maven_webmail/"
    }
    -->
</script>

<%
            if (session.isNew()) {
                session.setAttribute("host", application.getInitParameter("host"));   // should be modified if you change the POP3 server
                session.setAttribute("debug", "false");
            }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>비밀번호 재설정 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    
    <body>
        <%@include file="header.jspf" %>
        <div id="login_form">
            <form method="POST" action="ResetPassword.do">
                --- 비밀번호 재설정 --- <br/>
                아이디: <input type="text" name="userid" size="20" required> <br />
                이&nbsp;&nbsp;&nbsp;름: <input type="text" name="username" size="20" required> <br />
                변경할 비밀번호: <input type="password" name="password" size="20" required> <br /><br />
                
                <input type="submit" value="비밀번호 재설정" name="B1">&nbsp;&nbsp;&nbsp;
                <input type="button" value="돌아가기" name="B2" onclick="gohome();">
            </form>
        </div>
        <%@include file="footer.jspf" %>
    </body>
</html>
