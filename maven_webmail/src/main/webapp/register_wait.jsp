<%-- 
    Document   : register_wait
    Created on : 2021. 5. 24., 오후 6:25:02
    Author     : 김기목
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>회원가입 성공 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:include page="header.jsp" />
    <center>
        <p>
            회원가입 성공! 지금바로 로그인 해보세요!
        </p>
        
        <p>    <a href ="index.jsp"> 로그인 페이지로 이동하기.</a>
            
            
        </p>
        </center>
        
        
        
        
         <jsp:include page="footer.jsp" />
    </body>
</html>
