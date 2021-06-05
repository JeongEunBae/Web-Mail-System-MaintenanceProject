/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cse.maven_webmail.model.Pop3Agent;

/**
 *
 * @author jongmin, cheongil
 */
public class LoginHandler extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private static final String ADMINISTRATOR = "admin";
    private static final String USERID = "userid";
    private static final String PASSWD = "passwd";
    private Log log = LogFactory.getLog(LoginHandler.class);

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        int selectedMenu = Integer.parseInt((String) request.getParameter("menu"));


        try {
            switch (selectedMenu) {
                case CommandType.LOGIN:
                    String host = (String) request.getSession().getAttribute("host");
                    String userid = request.getParameter(USERID);
                    String password = request.getParameter(PASSWD);

                    // Check the login information is valid using <<model>>Pop3Agent.
                    Pop3Agent pop3Agent = new Pop3Agent(host, userid, password);
                    boolean isLoginSuccess = pop3Agent.validate();

                    // Now call the correct page according to its validation result.
                    if (isLoginSuccess) {
                        log.info("LoginSuccess");
                        if (isAdmin(userid)) {
                            // HttpSession 객체에 userid를 등록해 둔다.
                            session.setAttribute(USERID, userid);
                            response.sendRedirect("admin_menu.jsp");
                        } else {
                            // HttpSession 객체에 userid와 password를 등록해 둔다.
                            session.setAttribute(USERID, userid);
                            session.setAttribute(PASSWD, password);
                            response.sendRedirect("main_menu.jsp");
                        }
                    } else {
                        RequestDispatcher view = request.getRequestDispatcher("login_fail.jsp");
                        view.forward(request, response);
                    }
                    break;
                case CommandType.LOGOUT:
                    out = response.getWriter();
                    session.invalidate();
                    response.sendRedirect(getServletContext().getInitParameter("HomeDirectory"));
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            log.error("LoginCheck - LOGIN error: " + ex);
        } finally {
            out.close();
        }
    }

    protected boolean isAdmin(String userid) {
        boolean status = false;

        if (userid.equals(ADMINISTRATOR)) {
            status = true;
        }

        return status;
    }
// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException,
            IOException {
        processRequest(request, response);


    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException,
            IOException {
        processRequest(request, response);


    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";

    }// </editor-fold>
}
