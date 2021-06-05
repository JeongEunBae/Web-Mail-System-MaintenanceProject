package cse.maven_webmail.control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// S1128
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
import java.sql.Statement;
// S1128

import javax.servlet.annotation.WebServlet;
import javax.swing.Popup;
// S1128

@WebServlet(name = "UserAddHandler", urlPatterns = {"/UserAddhandler.do"})
/**
 * 
 * @author 김기목
 */

public class UserAddHandler extends HttpServlet {

    Connection conn = null;
    Statement stmt = null;
    PreparedStatement pstmt = null;
    Log log = LogFactory.getLog(UserAddHandler.class);

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
                adduser(request, out);
            }
        } catch (Exception ex) {
             Log log = LogFactory.getLog(UserAddHandler.class); 
            log.error(ex.toString());
        }
    }

    private void adduser(HttpServletRequest request, PrintWriter out) { // S1172          
                
        try {
            String name = "java:/comp/env/jdbc/user_register";
            javax.naming.Context ctx = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx.lookup(name);
            
            String registerId = request.getParameter("register_id");
            String registerPW = request.getParameter("register_pw_check");
            String registerName = request.getParameter("register_name");
            String registerNumber = request.getParameter("register_number");
            int reqCK = 0;
            
            conn = ds.getConnection();
            
            String sql = "INSERT INTO user_register VALUES(?,?,?,?,?)";
            
            
            pstmt = conn.prepareStatement(sql); // S117
            pstmt.setString(1, registerId);
            pstmt.setString(2, registerPW);
            pstmt.setString(3, registerName);
            pstmt.setString(4, registerNumber);
            pstmt.setInt(5,reqCK);
            
            int queryState = pstmt.executeUpdate();

            name = "java:/comp/env/jdbc/fakeletter";
            sql = "INSERT INTO fakeletter(userid) VALUES(?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, registerId);
            pstmt.executeUpdate();
            
            if (queryState >= 1) {
                StringBuilder popup = new StringBuilder();
                popup.append("<script>alert('회원가입 완료 승인대기중'); location.href='register_wait.jsp';</script>");
                out.println(popup);
                String popupLog = popup.toString();
                log.info(popupLog);
            } else {
                StringBuilder popup = new StringBuilder();
                popup.append("<script>alert('서버 상태를 확인하세요.'); window.history.back();</script>");
                out.println(popup);
                String popupLog = popup.toString();
                log.info(popupLog);
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
