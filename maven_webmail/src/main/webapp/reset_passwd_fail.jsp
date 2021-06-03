<%-- 
    Document   : reset_passwd_fail
    Created on : 2021. 5. 24., 오후 11:31:55
    Author     : chung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<script type="text/javascript">
    <!--
    function gohome() {
        window.location = "/maven_webmail/"
    }
    -->
</script>


<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>비밀번호 재설정 실패</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body onload="setTimeout('gohome()', 5000)">
        <jsp:include page="header.jsp" />
        
        <p id="reset_passwd_fail">
            비밀번호 재설정에 실패하였습니다. <br/>
            
            사용자 ID와 이름을 확인하여 주시기 바랍니다. <br/>
            
            5초 뒤 자동으로 초기 화면으로 돌아갑니다. <br/>
            
            자동으로 화면 전환이 일어나지 않을 경우 
            <a href="<%= getServletContext().getInitParameter("HomeDirectory") %>" title="초기화면">초기 화면</a>을 선택해주세요.
        </p>
        
        <jsp:include page="footer.jsp" />
        
    </body>
</html>
