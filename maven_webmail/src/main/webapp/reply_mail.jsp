<%-- 
    Document   : reply_mail.jsp
    Created on : 2021. 5. 19., 오전 9:35:25
    Author     : BaeJeongEun
--%>

<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.CommandType" %>

<!DOCTYPE html>
<jsp:useBean id="reply" scope="page" class="cse.maven_webmail.model.ReplyMailAgent" /> 
<%
    reply.setHost((String) session.getAttribute("host"));
    reply.setUserid((String) session.getAttribute("userid"));
    reply.setPassword((String) session.getAttribute("password"));
%>

<%--@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" --%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>메일 답장 쓰기 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <%
            String messageIndex = String.valueOf(session.getAttribute("messageNumber"));
            reply.saveMessage(Integer.parseInt(messageIndex));
        %>
        <jsp:include page="header.jsp" />
        <div id="sidebar">
            <jsp:include page="sidebar_previous_menu.jsp" />
        </div>
        
        <div id="main">
            <form enctype="multipart/form-data" method="POST"
                  action="WriteMail.do?menu=<%= CommandType.SEND_MAIL_COMMAND%>" >
                <table>
                    <tr>
                        <td> 수신 </td>
                        <td> <input type="text" name="to" size="80"
                                    value=<%=reply.getFromAddress()== null ? "" : reply.getFromAddress()%>>  </td>
                    </tr>
                    <tr>
                        <td>참조</td>
                        <td> <input type="text" name="cc" size="80" value="<%=reply.getCcAddress()== null ? "" : reply.getCcAddress()%>">  </td>
                    </tr>
                    <tr>
                        <td> 메일 제목 </td>
                        <td> <input type="text" name="subj" size="80" value="RE: <%=reply.getSubject()==null ? "" : reply.getSubject() %>"></td>
                    </tr>
                    <tr>
                        <td colspan="2">본  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 문</td>
                    </tr>
                    <tr>  <%-- TextArea    --%>
                        <td colspan="2">  
                            <textarea rows="15" name="body" cols="80"></textarea> 
                        </td>
                    </tr>
                    <tr>
                        <td>첨부 파일</td>
                        <td> <input type="file" name="file1"  size="80" value="<%=reply.getFileName()==null ? "" : reply.getFileName() %>">  </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="submit" value="메일 보내기">
                            <input type="reset" value="다시 입력">
                        </td>
                    </tr>
                </table>
            </form>
        </div>

        <jsp:include page="footer.jsp" />
    </body>
</html>
