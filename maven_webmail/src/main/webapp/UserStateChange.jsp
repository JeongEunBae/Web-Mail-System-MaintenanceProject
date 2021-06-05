<%-- 
    Document   : UserStateChange
    Created on : 2021. 5. 30., 오전 1:20:35
    Author     : 김기목
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import = "java.sql.*"%>
<%@page errorPage="error_page.jsp"%>


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>승인여부 변경 페이지</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
      <body>
        <jsp:include page="header.jsp" />
        <div id="sidebar">
            <jsp:include page="sidebar_admin_previous_menu.jsp" />
        </div>
        <form action="UserAccept.do" method="POST">           
            <br>
            변경할 사용자의 ID값을 입력하세요 :  <input type="text" name="change_id" size="20">
             <input type="submit" name="request_accept" value= "승인"/>
        
        </form>
        

        <table border="2">
        <tr>
            <th>아이디</th>
            <th>승인 여부</th>
        </tr>
       
        <p> 미승인 사용자 ID 목록 </p>
      <tbody>
        <%
            try{
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
                
                String sql = "select id,accept_check from user_register where accept_check = 0 ";
                ResultSet rs = stmt.executeQuery(sql); 
                %>
                <%
            while (rs.next()){
 
                            out.println("<tr>");
                            out.println("<td>" + rs.getString("id")+"</td>");
                            
                            int accpt_ck = rs.getInt("accept_check");
                            String show_ck = null;
                            
                            switch(accpt_ck) {
                                case 0: 
                                    show_ck = "회원가입 승인 전";
                                        break;
                                case 1: 
                                    show_ck = "회원가입 승인 완료";
                                    break;     
                                }
                            out.println("<td>"+show_ck +"</td>");
                            out.println("<tr>");

                        } 
                        rs.close();
                        stmt.close();
                        conn.close(); 
                        
                }catch(Exception ex) {
                        out.println("오류발생 . 발생 오류 : " + ex.getMessage() + ")");
                        }  
           %>
                           </tbody>
           </table>  
         <jsp:include page="footer.jsp" />
    </body>
</html>
