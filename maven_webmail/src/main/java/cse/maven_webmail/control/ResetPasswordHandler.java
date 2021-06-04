/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cse.maven_webmail.model.ResetPasswordAgent;
import javax.servlet.RequestDispatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author chung
 */
public class ResetPasswordHandler extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    private String userid = "";
    private String passwd = "";
    Log log = LogFactory.getLog(ResetPasswordHandler.class);
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try (PrintWriter out = response.getWriter()) {
            request.setCharacterEncoding("UTF-8");
            
            userid = request.getParameter("userid");
            passwd = request.getParameter("password");
            String inputname = request.getParameter("username");
            
            String username = ""; // 데이터베이스의 이름 가져오기
            String name = "java:/comp/env/jdbc/passwd_reset";
            javax.naming.Context ctx = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx.lookup(name);
            
            try(conn = ds.getConnection()) { // S2095
                try(stmt = conn.createStatement()) {
                    String sql = "SELECT name FROM passwd_reset WHERE id=" + "\'" + userid + "\'";

                    rs = stmt.executeQuery(sql);

                    while(rs.next()){
                        username = rs.getString("name");
                    }

                    if(userid == null || passwd == null || !inputname.equals(username)){
                        RequestDispatcher view = request.getRequestDispatcher("reset_passwd_fail.jsp");
                        view.forward(request, response);
                    } else {
                        resetPasswd(request, response, out);
                    }
                }
            }
            
        } catch (Exception ex){
            log.error("ResetPasswordHandler error: " + ex);
        } finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            if(conn != null)
                conn.close();
        }
    }
    
    private boolean resetPasswd(HttpServletRequest request, HttpServletResponse response, PrintWriter out){
        String server = request.getServerName();
        boolean status = false;
        int port = 4555;
        try{
            ResetPasswordAgent agent = new ResetPasswordAgent(server, port, this.getServletContext().getRealPath("."));
            if(agent.resetPasswd(userid, passwd)){
                out.print(getResetSuccessPopup());
                status = true;
            } else {
                RequestDispatcher view = request.getRequestDispatcher("reset_passwd_fail.jsp");
                view.forward(request, response);
            }
        } catch (Exception ex){
            log.error("ResetPasswordCheck - ResetPassword error: " + ex);
        } finally {
            out.close();
        }
        return status;
    }
    
    private String getResetSuccessPopup(){
        String alertMessage = "비밀번호 재설정이 완료되었습니다.";
        StringBuilder successPopUp = new StringBuilder();
        successPopUp.append("<html>");
        successPopUp.append("<head>");

        successPopUp.append("<title>비밀번호 재설정 성공</title>");
        successPopUp.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main_style.css\" />");
        successPopUp.append("</head>");
        successPopUp.append("<body onload=\"goHome()\">");
        successPopUp.append("<script type=\"text/javascript\">");
        successPopUp.append("function goHome() {");
        successPopUp.append("alert(\"");
        successPopUp.append(alertMessage);
        successPopUp.append("\"); ");
        successPopUp.append("window.location = \"/maven_webmail\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(ResetPasswordHandler.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ResetPasswordHandler.class.getName()).log(Level.SEVERE, null, ex);
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
