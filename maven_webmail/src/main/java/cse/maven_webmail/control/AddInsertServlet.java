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
public class AddInsertServlet extends HttpServlet {

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
            
            Class.forName(JdbcDriver); // JDBC 드라이버 적재

            // Connection 객체 생성
            try(Connection conn = DriverManager.getConnection(JdbcUrl, User, password)) { // S2095

                // PreparedStatement 객체 생성
                // S125
                String sql = "INSERT INTO `addrbook` (`username`, `add_name`, `add_email`, `add_tel`) VALUES (?,?,?,?)";
                 try(PreparedStatement pstmt = conn.prepareStatement(sql)) { // S2095
                
                    // userid 값 받아오기 위한 세션
                    HttpSession httpSession = request.getSession();

                    // SQL 문 완성
                    request.setCharacterEncoding("UTF-8");
                    String userid = (String) httpSession.getAttribute("userid");
                    if ((userid != null) || !userid.equals("")) { // S1940
                        String name = request.getParameter("add_name");
                        String email = request.getParameter("add_email");
                        String tel = request.getParameter("add_tel");

                        pstmt.setString(1, userid);
                        pstmt.setString(2, name);
                        pstmt.setString(3, email);
                        pstmt.setString(4, tel);
                        pstmt.executeUpdate();
                    }

                    // TEST
                    out.println(successPopUp());

                    // S125
                }
            } catch (Exception ex) {
                out.println(failurePopUp());
                out.println("오류가 발생했습니다.(발생 오류 : " + ex.getMessage() + ")");
                out.println("<br/> <a href=\"addrbook_list.jsp\">초기 화면으로 가기</a>");
            }finally {
                pstmt.close();
                conn.close();
            }
        } finally {
            out.close();
        }
    }

    private String successPopUp() { // S100
        String alertMessage = "주소록 등록이 성공했습니다.";
        StringBuilder successpopup = new StringBuilder();
        successpopup.append("<html>");
        successpopup.append("<head>");

        successpopup.append("<title>메일 전송 결과</title>");
        successpopup.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main_style.css\" />");
        successpopup.append("</head>");
        successpopup.append("<body onload=\"goMainMenu()\">");
        successpopup.append("<script type=\"text/javascript\">");
        successpopup.append("function goMainMenu() {");
        successpopup.append("alert(\"");
        successpopup.append(alertMessage);
        successpopup.append("\"); ");
        successpopup.append("window.location = \"addrbook_list.jsp\"; ");
        successpopup.append("}  </script>");
        successpopup.append("</body></html>");
        return successpopup.toString();
    }

    private String failurePopUp() { //S100
        String alertMessage = "주소록 등록이 실패했습니다.";
        StringBuilder successpopup = new StringBuilder();
        successpopup.append("<html>");
        successpopup.append("<head>");

        successpopup.append("<title>메일 전송 결과</title>");
        successpopup.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main_style.css\" />");
        successpopup.append("</head>");
        successpopup.append("<body onload=\"goMainMenu()\">");
        successpopup.append("<script type=\"text/javascript\">");
        successpopup.append("function goMainMenu() {");
        successpopup.append("alert(\"");
        successpopup.append(alertMessage);
        successpopup.append("\"); ");
        successpopup.append("}  </script>");
        successpopup.append("</body></html>");
        return successpopup.toString();
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
        return "SQL INSERT 문을 사용한 DB갱신";
    }// </editor-fold>

}
