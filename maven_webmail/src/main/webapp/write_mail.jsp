<%--
    Document   : write_mail.jsp
    Author     : jongmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.CommandType" %>

<!DOCTYPE html>

<%-- @taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" --%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>타인에게 메일 쓰기 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <style>
        div#mw01{
            background-color: palegoldenrod;
            border: 2px solid black;
            display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;
        }
            table,th,
                td
                {
                    border:3px solid black; 
                }
    </style>
    <body>
        <jsp:include page="header.jsp" />

        <div id="sidebar_write">
            <jsp:include page="sidebar_previous_menu_write.jsp" />
        </div>

        <div id="main">
            <form enctype="multipart/form-data" method="POST"
                  action="WriteMail.do?menu=<%= CommandType.SEND_MAIL_COMMAND%>" > 
                
                <div id="mw01">
                <h1>◇ 상대방에게 메일 쓰기 ◇</h1>
                <table class="table table-striped" style="background-color: white; text-align:center; border: 1px solid black">
                    <thread>
                        <tr>
                            <th style="background-color: crimson; text-align:center;">메일 주소</th>
                            <td> <input type="text" name="to" size="30"
                                        value=<%=request.getParameter("recv") == null ? "" : request.getParameter("recv")%> >  </td>
                            <th style="background-color: background; text-align:center;">참 조</th>
                            <td> <input type="text" name="cc" size="30">  </td>
                            <th style="background-color: background; text-align:center;">메일 제목</th>
                            <td> <input type="text" name="subj" size="30">  </td>
                            <th style="background-color: background; text-align:center;">첨부 파일</th>
                            <td> <input type="file" name="file1"  size="30">  </td>
                        </tr>
                    </thread>
                        <tr>
                            <td colspan="20">본   문</td>
                        </tr>
                        <tr>  <%-- TextArea    --%>
                            <td colspan="20">  <textarea style="width:1300px; resize: none;" rows="10" name="body" cols="40"></textarea> </td>
                        </tr>
                        <tr>
                            <td colspan="20">
                                <input type="submit" value="메일 보내기">
                                <input type="reset" value="다시 입력">
                            </td>
                        </tr>
                </table>
            </form>
        </div>
        </div>

        <jsp:include page="footer.jsp" /> 
    </body>
</html>
