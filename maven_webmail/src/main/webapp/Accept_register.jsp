<%-- 
    Document   : Accept_register
    Created on : 2021. 5. 15., 오후 3:19:09
    Author     : 김기목
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>회원가입 승인 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:include page="header.jsp" />
         <div id="sidebar">
            <jsp:include page="sidebar_admin_previous_menu.jsp" />
        </div>
        
        <h1>회원가입 신청 사용자 목록</h1>
        <table border="0" align="left">
                    <tr>
                        <td>선택</td>
                        <td>ID</td>
                        <td>PW</td>
                        <td>이름</td>
                        <td>전화번호</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="chk"/></td>
                        <td> <input type="text" name="id" value="" /></td>
                        <td> <input type="text" name="password" value="" /> </td>
                        <td> <input type="text" name="name" value="" /></td>
                        <td> <input type="text" name="phone_num" value="" /> </td>
                    </tr>
                    <tr>
                        <td colspan="5">
                            <input type="submit" value="확인"/>
                        </td>
                    </tr>
                   
                </table>
         <jsp:include page="footer.jsp" />
    </body>
</html>
