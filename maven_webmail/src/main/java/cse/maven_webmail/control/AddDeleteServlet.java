/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// S1128

/**
 *
 * @author JIUK
 */
public class AddDeleteServlet extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        try {
            final String JdbcDriver = "com.mysql.jdbc.Driver";
            final String JdbcUrl = "jdbc:mysql://113.198.235.241:3192/web-mailsystem";
            final String User = "webmailuser";
            final String password = "12345";
            
            Class.forName(JdbcDriver); // JDBC 드라이버 적재

            // Connection 객체 생성
            try(Connection conn = DriverManager.getConnection(JdbcUrl, User, password)) { // S2095

                // PreparedStatement 객체 생성
                String sql = "DELETE FROM addrbook WHERE idx=?";
                try(PreparedStatement pstmt = conn.prepareStatement(sql)) { // S2095
                    //4. SQL문 완성
                    request.setCharacterEncoding("UTF-8");
                    String addIdx = request.getParameter("idx"); //get방식으로 받아옴 // S117
                    int idx = Integer.parseInt(addIdx);
                    pstmt.setInt(1, idx);

                    // 5. 실행
                    pstmt.executeUpdate(); // 테이블에 변화가 생길때만 사용함
                    response.sendRedirect("addrbook_list.jsp"); //삭제가 정상적으로 이루워지면 addrbook_list.jsp로 돌아간다
                }                

            } catch (Exception ex) {
                out.println("오류가 발생했습니다 (발생오류:" + ex.getMessage() + ")");
            }finally {
                pstmt.close(); // S2095
                conn.close();
            }
        } finally {
            out.close();
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
