<%-- 
    Document   : Accept_register
    Created on : 2021. 5. 15., 오후 3:19:09
    Author     : 김기목
--%>

<%@page import="cse.maven_webmail.control.CommandType"%>
<%@page import="java.sql.Connection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import = "java.sql.*"%>

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

        <%
            try {
                //String name = "java:/comp/env/jdbc/AddrBookDB";
                //javax.naming.Context ctx = new javax.naming.InitialContext();
                //javax.sql.DataSource ds = (javax.sql.DataSource)ctx.lookup(name);

                final String className = "com.mysql.cj.jdbc.Driver";
                Class.forName(className);
                final String url = "jdbc:mysql://113.198.235.241:3192/web-mailsystem?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=Asia/Seoul";
                final String User = "webmailuser";
                final String Password = "12345";

                Connection conn = DriverManager.getConnection(url, User, Password);
                //Connection conn = ds.getConnection();

                Statement stmt = conn.createStatement();

                String sql = "SELECT id,pw,name,tel,accept_check FROM user_register";
                ResultSet rs = stmt.executeQuery(sql);

        %>
        <form action="UserAccept.do" method="POST">
            <table border="1">
                <tr>
                    <th>아이디</th>
                    <th>비밀번호</th>
                    <th>이름</th>
                    <th>전화번호</th>
                    <th>회원가입 승인여부</th>
                </tr>



                <tbody>
                    <%                            while (rs.next()) {

                                out.println("<tr>");

                                out.println("<td>" + rs.getString("id") + "</td>");
                                out.println("<td>" + rs.getString("pw") + "</td>");
                                out.println("<td>" + rs.getString("name") + "</td>");
                                out.println("<td>" + rs.getString("tel") + "</td>");

                                int accpt_ck = rs.getInt("accept_check");
                                String show_ck = null;

                                switch (accpt_ck) {
                                    case 0:
                                        show_ck = "회원가입 승인 전";
                                        break;
                                    case 1:
                                        show_ck = "회원가입 승인 완료";
                                        break;
                                }
                                out.println("<td>" + show_ck + "</td>");
                                out.println("<tr>");

                            }
                            rs.close();
                            stmt.close();
                            conn.close();

                        } catch (Exception ex) {
                            out.println("오류발생 . 발생 오류 : " + ex.getMessage() + ")");
                        }
                    %>


                </tbody>
            </table>
            <br>
            <br>
            <center>       
                <strong>   <input type="button" value = "승인 하러 가기" onclick="location.href = 'UserStateChange.jsp'">   </strong>
            </center>
        </form>
        <jsp:include page="footer.jsp" />
    </body>
</html>
