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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.annotation.WebServlet;
import javax.swing.Popup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WebServlet(name = "UserAddHandler", urlPatterns = {"/UserAddhandler.do"})
/**
 * 
 * @author 김기목
 */

public class UserAddHandler extends HttpServlet {

    Connection conn = null;
    PreparedStatement pstmt = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            request.setCharacterEncoding("UTF-8");
            int select = Integer.parseInt((String) request.getParameter("menu"));

            if (select == CommandType.ADD_USER_COMMAND) {
                adduser(request, response, out);
            }
        } catch (Exception ex) {
             Log log = LogFactory.getLog(UserAddHandler.class); 
            log.error(ex.toString());
        }
    }

    private void adduser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {              
                
        try {
        
            String className = "com.mysql.cj.jdbc.Driver";
            Class.forName(className);
            String  url = "jdbc:mysql://localhost:3306/webmail?serverTimezone=Asia/Seoul";
            String User = "jdbctester";
            String Password = "1088";

            String register_id = request.getParameter("register_id");
            String register_pw = request.getParameter("register_pw_check");
            String register_name = request.getParameter("register_name");
            String register_number = request.getParameter("register_number");
            int req_ck = 0;
            
            conn = DriverManager.getConnection(url, User, Password);
            
            String sql = "INSERT INTO user_register VALUES(?,?,?,?,?)";
            
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, register_id);
            pstmt.setString(2, register_pw);
            pstmt.setString(3, register_name);
            pstmt.setString(4, register_number);
            pstmt.setInt(5,req_ck);
            
            int query_state = pstmt.executeUpdate();

            if (query_state >= 1) {
                StringBuilder Popup = new StringBuilder();
                Popup.append("<script>alert('회원가입 완료 승인대기중'); location.href='register_wait.jsp';</script>");
                out.println(Popup.toString());
            } else {
                StringBuilder Popup = new StringBuilder();
                Popup.append("<script>alert('서버 상태를 확인하세요.'); window.history.back();</script>");
                out.println(Popup.toString());
            }
            pstmt.close();
            conn.close();
            out.flush();
        } catch (Exception ex) {
          
            out.println(ex.getMessage());
            ex.printStackTrace();
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
        processRequest(request, response);
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
        processRequest(request, response);
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