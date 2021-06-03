<%-- 
    Document   : addrbook_update_form.jsp
    Created on : 2021. 5. 25., 오후 3:01:30
    Author     : JIUK
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>주소록 수정 폼</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:include page="header.jsp" />

        <div id="sidebar">
            <jsp:include page="sidebar_previous_menu.jsp" />
        </div>

        <h1> 주소록 수정 </h1>
        <%
            final String JdbcDriver = "com.mysql.jdbc.Driver";
            final String JdbcUrl = "jdbc:mysql://localhost:3306/jiuk";
            final String User = "root";
            final String password = "root";

            try {
                Class.forName(JdbcDriver);

                Connection conn = DriverManager.getConnection(JdbcUrl, User, password);

                Statement stmt = conn.createStatement();

                String sql = "SELECT add_name, add_email, add_tel FROM addrbook where idx='" + request.getParameter("idx") + "'";
                ResultSet rs = stmt.executeQuery(sql);

        %>
        <hr/>

        <form action="AddUpdateServlet" method="POST">
            <table border="0">
                <input type="hidden" id="idx" name="idx" value="<%=request.getParameter("idx")%>"/>
                <!-- idx값을 넘겨주기 위해서 input type을 hidden으로하여 post 방식으로 넘긴다. -->

                <tbody>
                    <%                            while (rs.next()) {
                            out.println("<tr>");
                            out.println("<td>이름</td>");
                            out.println("<td><input type=\"text\" name=\"add_name\" size=\"20\" value=\"" + rs.getString("add_name") + "\"/></td>");
                            out.println("</tr>");

                            out.println("<tr>");
                            out.println("<td>이메일</td>");
                            out.println("<td><input type=\"text\" name=\"add_email\" size=\"20\" value=\"" + rs.getString("add_email") + "\"/></td>");
                            out.println("</tr>");

                            out.println("<tr>");
                            out.println("<td>전화번호</td>");
                            out.println("<td><input type=\"text\" name=\"add_tel\" size=\"20\" value=\"" + rs.getString("add_tel") + "\"/></td>");
                            out.println("</tr>");

                            out.println("<tr>");
                            out.println("<td colspan=\"2\">");
                            out.println("<center> <input type=\"submit\" value=\"수정\"/>");
                            out.println("</center> </td>");
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
        </form>
        <br/>



        <jsp:include page="footer.jsp" />
    </body>
</html>
