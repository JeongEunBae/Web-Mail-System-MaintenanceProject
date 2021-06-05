package cse.maven_webmail.control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import cse.maven_webmail.model.UserAdminAgent;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WebServlet(name = "UserAccept", urlPatterns = {"/UserAccept.do"})
/*
 * 
 * @author 김기목
 *
 */

public class UserAccept extends HttpServlet {
    Connection conn = null;
    Connection con = null;
    PreparedStatement pstmt = null;
    PreparedStatement  r_pstmt = null;
    Statement stmt = null;
    StringBuilder Popup = new StringBuilder();
    ResultSet rs =null;
    ResultSet rs_2 = null;
    int query_state = 0;
    Log log = LogFactory.getLog(UserAccept.class);
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.sql.SQLException
     */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        
            try{

            //String className = "com.mysql.cj.jdbc.Driver";
            //Class.forName(className);
            //String url = "jdbc:mysql://localhost:3306/webmail?serverTimezone=Asia/Seoul";
            //String User = "jdbctester";
            //String Password = "1088";

            
            
            final String className = "com.mysql.cj.jdbc.Driver";
            Class.forName(className);
            final String url = "jdbc:mysql://113.198.235.241:3192/web-mailsystem?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=Asia/Seoul";
            final String User = "webmailuser";
            final String Password = "12345";
                
            conn = DriverManager.getConnection(url, User, Password); 
            
            String change_id = request.getParameter("change_id");
            
            String sql = "UPDATE user_register SET accept_check = ? WHERE id= ?";

            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, 1);
            pstmt.setString(2, change_id);
            query_state = pstmt.executeUpdate();

            con =  DriverManager.getConnection(url, User, Password);
            stmt  = con.createStatement();
            String plz_sql ="SELECT id,pw,accept_check FROM user_register WHERE accept_check=1"; 
            rs = stmt.executeQuery(plz_sql);   
            
            
            while(rs.next()){    
                    String server = "127.0.0.1";
                    int port = 4555;
                    
                    if(rs.getString("id")!=null && rs.getString("id").equals(change_id)){ // 여기가 입력받은 id값이랑 SQL커서값 동일한 id인지 확인.

                        PrintWriter out = response.getWriter();
                        UserAdminAgent agent = new UserAdminAgent(server, port, this.getServletContext().getRealPath("."));
                        String userid = rs.getString("id"); 
                        String password = rs.getString("pw");
                        log.info("userid = " + userid + "<br>");
                        log.info("password = " + password + "<br>");
                        log.info("회원가입 승인 성공!! 뒤로가기를 눌러주세요.");
                        out.flush();
                        if(agent.addUser(userid, password)) {
                           Popup.append("<script>alert('사용자 인증을 완료했습니다.'); location.href='UserStateChange.jsp;</script>");
                           log.info(Popup.toString());
                        } else {
                            Popup.append("<script>alert('사용자 등록에 실패했습니다.'); location.href='UserStateChange.jsp;</script>");
                            log.error(Popup.toString());
                        }
                        out.flush();
                            
                        
                        }
            }
                
        } catch (Exception ex) {
         Log log = LogFactory.getLog(UserAccept.class);
         log.error("시스템 접속에 실패했습니다.");
        }
            
       finally {
        if ( pstmt !=null ) {pstmt.close();}
        if ( conn !=null ) {conn.close();}
        if ( con!=null) {con.close();}
        if ( stmt!=null) {stmt.close();}

}  
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
/**
 * Handles the HTTP <code>GET</code> method.
 *
 * @param request servlet request
 * @param response servlet response
 * @throws ServletException if a servlet-specific error occurs
 * @throws IOException if an I/O error occurs
 */
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
