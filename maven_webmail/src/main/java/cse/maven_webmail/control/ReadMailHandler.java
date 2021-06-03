/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cse.maven_webmail.model.Pop3Agent;
import java.util.logging.LogManager;

/**
 *
 * @author jongmin
 */
public class ReadMailHandler extends HttpServlet {
    private static final String CHARACTER_ENCODING_SET = "UTF-8"; // ADD JEONGEUN

    private static Logger logger = LoggerFactory.getLogger(ReadMailHandler.class);// ADD JEONGEUN
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        request.setCharacterEncoding(CHARACTER_ENCODING_SET); // ADD JEONGEUN
        int select = Integer.parseInt((String) request.getParameter("menu"));

        switch (select) {
            case CommandType.DELETE_MAIL_COMMAND:
                try (PrintWriter out = response.getWriter()) {
                    deleteTempMessage(request);
                    response.sendRedirect("main_menu.jsp");
                }
                break;
                
            case CommandType.DELETE_TEMP_MAIL_COMMAND:
                try (PrintWriter out = response.getWriter()) {
                    deleteMessage(request);
                    response.sendRedirect("main_menu.jsp");
                }
                break; 
                
            case CommandType.DOWNLOAD_COMMAND: // 파일 다운로드 처리
                download(request, response);
                break;

            default:
                try (PrintWriter out = response.getWriter()) {
                    out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                }
                break;

        }
    }

    private void download(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/octet-stream");

        ServletOutputStream sos = null;

        try { // MODIFY JEONGEUN
            request.setCharacterEncoding(CHARACTER_ENCODING_SET); // ADD JEONGEUN
            // LJM 041203 - 아래와 같이 해서 한글파일명 제대로 인식되는 것 확인했음.
            String fileName = request.getParameter("filename");
            logger.trace(">>>>>> DOWNLOAD: file name = " + fileName); // ADD JEONGEUN
            String userid = request.getParameter("userid");

            // download할 파일 읽기

            // 윈도우즈 환경 사용시
            String downloadDir = "C:/temp/download/";
            if (System.getProperty("os.name").equals("Linux")) {
                downloadDir = request.getServletContext().getRealPath("/WEB-INF") 
                        + File.separator + "download";
                File f = new File(downloadDir);
                if (!f.exists()) {
                    f.mkdir();
                }
            }

            response.setHeader("Content-Disposition", "attachment; filename="
                    + URLEncoder.encode(fileName, CHARACTER_ENCODING_SET) + ";"); // ADD JEONGEUN

            File f = new File(downloadDir + File.separator + userid + File.separator + fileName);
            byte[] b = new byte[(int) f.length()];
            // try-with-resource 문은 fis를 명시적으로 close해 주지 않아도 됨.
            try (FileInputStream fis = new FileInputStream(f)) {
                int count = 0;
                while((count = fis.read(b)) > 0) { // ADD JEONGEUN
                }
            } // MODIFY JEONGEUN

            // 다운로드
            sos = response.getOutputStream();
            sos.write(b);
            sos.flush();
            sos.close();
        } catch (Exception ex) {
            logger.trace("====== DOWNLOAD exception : " + ex); // ADD JEONGEUN
        }
    }

    private boolean deleteMessage(HttpServletRequest request) {
        System.out.println("=============deleteMessage===========");
        int msgid = Integer.parseInt((String) request.getParameter("msgid"));
        HttpSession httpSession = request.getSession();
        String host = (String) httpSession.getAttribute("host");
        String userid = (String) httpSession.getAttribute("userid");
        String password = (String) httpSession.getAttribute("password");

        Pop3Agent pop3 = new Pop3Agent(host, userid, password);
        return pop3.deleteMessage(msgid, true); // MODIFY JEONGEUN
    }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    
    private boolean deleteTempMessage(HttpServletRequest request) {
        System.out.println("=============deleteTempMessage===========");
        int msgid = Integer.parseInt((String) request.getParameter("msgid"));
        HttpSession httpSession = request.getSession();
        String host = (String) httpSession.getAttribute("host");
        String userid = (String) httpSession.getAttribute("userid");
        String password = (String) httpSession.getAttribute("password");

        Pop3Agent pop3 = new Pop3Agent(host, userid, password);
        boolean status = pop3.deleteMessageFake(msgid);
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
            throws ServletException, IOException {
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
            throws ServletException, IOException {
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
