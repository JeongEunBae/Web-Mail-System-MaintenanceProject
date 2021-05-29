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
import javax.servlet.http.HttpSession;

/**
 *
 * @author JIUK
 */
public class AddUpdateServlet extends HttpServlet {

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
            final String JdbcUrl = "jdbc:mysql://localhost:3306/jiuk";
            final String User = "root";
            final String password = "root";

            try {
                // JDBC 드라이버 적재
                Class.forName(JdbcDriver);

                // Connection 객체 생성
                Connection conn = DriverManager.getConnection(JdbcUrl, User, password);

                // PreparedStatement 객체 생성
                // String sql ="INSERT INTO addrbook (username, add_name, add_email, add_tel) values (?,?,?,?)";
                // String sql = "UPDATE addrbook SET add_name=?, add_email=?, add_tel=? WHERE idx=?";
                String sql = "UPDATE `addrbook` SET `add_name` = ?, `add_email` = ?, `add_tel` = ? WHERE (`idx` = ?)";

                PreparedStatement pstmt = conn.prepareStatement(sql);

                // SQL 문 완성
                request.setCharacterEncoding("UTF-8");

                String name = request.getParameter("add_name");
                String email = request.getParameter("add_email");
                String tel = request.getParameter("add_tel");
                String add_idx = request.getParameter("idx");
                int idx = Integer.parseInt(add_idx);

                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, tel);
                pstmt.setInt(4, idx);

                pstmt.executeUpdate(); // 테이블에 변화가 생길때만 사용함
                pstmt.close();
                conn.close();

                // TEST
                //out.println(SuccessPopUp());
                response.sendRedirect("addrbook_list.jsp");

            } catch (Exception ex) {

                out.println("오류가 발생했습니다.(발생 오류 : " + ex.getMessage() + ")");
                out.println("<br/> <a href=\"addrbook_list.jsp\">초기 화면으로 가기</a>");
                out.println("TEST >> " + request.getParameter("idx"));
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