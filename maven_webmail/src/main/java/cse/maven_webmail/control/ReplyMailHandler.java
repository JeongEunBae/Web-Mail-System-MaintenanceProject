/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
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
 * @author byg09
 */
public class ReplyMailHandler extends HttpServlet {

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
            /* TODO output your page here. You may use following sample code. */
            final String JdbcDriver = "com.mysql.cj.jdbc.Driver";
            final String JdbcUrl = "jdbc:mysql://113.198.235.241:3192/web-mailsystem?serverTimezone=Asia/Seoul";
            final String User = "webmailuser";
            final String Password = "12345";

            try {
                //1. JDBC드라이버 적재
                Class.forName(JdbcDriver);

                //2. DB 연결
                Connection conn = DriverManager.getConnection(JdbcUrl, User, Password);

                //3. PreparedStatement 생성
                String sql = "INSERT INTO reply_mail(UserID, ToAddress, FromAddress, Title, Content, SendDate, DeleteMark, ReplyNo) values(?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);

                //4. SQL ans dhkstjd
                request.setCharacterEncoding("UTF-8"); // 한글 인식
                String userid = (String) request.getSession().getAttribute("userid");
                if (!(userid == null) && !userid.equals("")) {
                    String toAddress = request.getParameter("to");
                    String ccAddess = request.getParameter("cc");
                    String title = request.getParameter("title");
                    String content = request.getParameter("content");
                    String senddate = request.getParameter("send_time");
                    String delete_mark = request.getParameter("delete_mark");
                    String reply_no = request.getParameter("reply_no");
                    
                    pstmt.setString(1, userid);
                    pstmt.setString(2, toAddress);
                    pstmt.setString(3, ccAddess);
                    pstmt.setString(4, title);
                    pstmt.setString(5, content);
                    pstmt.setString(6, senddate);
                    pstmt.setString(7, delete_mark);
                    pstmt.setString(8, reply_no);
                    
                    // 5. 실행 : PreparedStatement.executeUpdate()는
                    // INSERT, UPDATE 또는 DELETE 시 사용 가능함.
                    pstmt.executeUpdate();
                }
                // 6. 자원 해제 
                pstmt.close();
                conn.close();

                response.sendRedirect("reply_mail.jsp");
            } catch (Exception ex) {
                out.println("오류가 발생했습니다. (발생 오류: " + ex.getMessage() + ")");
                out.println("<br /> <a href=\"main_menu.jsp\">메일 목록 화면으로 가기</a>");
            }
        }finally {
              out.close();
        }
    }

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
