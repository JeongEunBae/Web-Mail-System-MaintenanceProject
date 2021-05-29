<%-- 
    Document   : addrbook_form.jsp
    Created on : 2021. 5. 11., 오후 2:41:07
    Author     : JIUK
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>주소록 추가 폼</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:include page="header.jsp" />

        <h1> 주소록 추가 </h1>
        <hr/>

        <form action="AddInsertServlet" method="POST">
            <table border="0">

                <tbody>
                    <tr>
                        <td>이름</td>
                        <td><input type="text" name="add_name" size="20" /></td>
                    </tr>
                    <tr>
                        <td>이메일</td>
                        <td><input type="text" name="add_email" size="20" /></td>
                    </tr>
                    <tr>
                        <td>전화번호</td>
                        <td><input type="text" name="add_tel" size="20" /></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                <center>
                    <input type="submit" value="추가" />
                    <input type="reset" value="초기화" />
                </center>
                </td>
                </tr>
                </tbody>
            </table>
        </form>
    </body>
</html>




