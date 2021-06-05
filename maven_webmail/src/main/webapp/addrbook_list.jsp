<%-- 
    Document   : addrbook_list
    Created on : 2021. 5. 7., 오후 6:08:17
    Author     : JIUK
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%>
<%@page errorPage="error_page.jsp"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>주소록 목록</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />

    </head>
    <body>
        <jsp:include page="header.jsp" />
        
         <div id="sidebar">
            <jsp:include page="sidebar_addrbook_menu.jsp" />
        </div>
       
        <h1> 주소록 </h1>
     
        <%
            final String JdbcDriver = "com.mysql.jdbc.Driver";
            final String JdbcUrl = "jdbc:mysql://113.198.235.241:3192/web-mailsystem";
            final String User = "webmailuser";
            final String password = "12345";

            try {
                Class.forName(JdbcDriver);

                Connection conn = DriverManager.getConnection(JdbcUrl, User, password);

                Statement stmt = conn.createStatement();

                String sql = "SELECT idx, add_name, add_email, add_tel FROM addrbook where username='" + session.getAttribute("id") + "'"; 
                ResultSet rs = stmt.executeQuery(sql);

        %>

        <table border ="1">
            <thead>
                <tr>
                    <th>이름</th>
                    <th>이메일</th>
                    <th>전화번호</th>
                    <th>비고</th>
                </tr>
            </thead>
            <tbody>
                
                <%                            while (rs.next()) {
                        out.println("<tr>");
                        out.println("<td>" + rs.getString("add_name") + "</td>");
                        out.println("<td>" + rs.getString("add_email") + "</td>");
                        out.println("<td>" + rs.getString("add_tel") + "</td>");
                        
                        out.println("<td> <a href='AddDeleteServlet?idx=" + rs.getString("idx") + "'>");
                        out.println("<button>삭제</button>");
                        out.println("</a></td>");
                        
                        
                        out.println("</tr>");
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                %>
            </tbody>
        </table>
        <%
            } catch (Exception ex) {
                out.println("오류가 발생했습니다. (발생 오류:" + ex.getMessage() + ")");
            }

        %>
        <br/>
        
    

        <jsp:include page="footer.jsp" />
    </body>
</html>