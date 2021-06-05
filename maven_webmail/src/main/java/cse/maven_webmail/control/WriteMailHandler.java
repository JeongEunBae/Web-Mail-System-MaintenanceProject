/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cse.maven_webmail.model.FormParser;
import cse.maven_webmail.model.SmtpAgent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jongmin
 */

// 메일 전송에 필요한 준비만 하는 클래스

public class WriteMailHandler extends HttpServlet 
{

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    Log log = LogFactory.getLog(WriteMailHandler.class);
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        boolean status;
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;
        try
        {
            request.setCharacterEncoding("UTF-8");
            int select = Integer.parseInt((String) request.getParameter("menu"));
            switch (select)
            {
                case CommandType.SEND_MAIL_COMMAND: // 상대방에게 전송
                    out = response.getWriter();
                    status = sendMessage(request);
                    log.info(getMailTransportPopUp(status));
                    break;
                    
                case CommandType.SEND_MAIL_COMMAND_ME: // 내게 전송
                    out = response.getWriter();
                    status = sendMessageMe(request);
                    log.info(getMailTransportPopUp(status));
                    break;

                default:
                    out = response.getWriter();
                    log.error("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                    break;
            }
        }
        catch (Exception ex)
        {
            if(out != null)
                log.error(ex.toString());
        } finally
        {
            if(out != null)
                out.close();
        }
    }

    private boolean sendMessage(HttpServletRequest request)
    {
        boolean status = false;
        try {
            
            // 1. toAddress, ccAddress, subject, body, file1 정보를 파싱하여 추출
            FormParser parser = new FormParser(request);
            parser.parse();
            
            // 2.  request 객체에서 HttpSession 객체 얻기
            HttpSession session = (HttpSession) request.getSession();
            
            // 3. HttpSession 객체에서 메일 서버, 메일 사용자 ID 정보 얻기
            String host = (String) session.getAttribute("host");
            String userid = (String) session.getAttribute("userid");
            
            // 4. SmtpAgent 객체에 메일 관련 정보 설정
            SmtpAgent agent = new SmtpAgent(host, userid);
            agent.setTo(parser.getToAddress());
            agent.setCc(parser.getCcAddress());
            agent.setSubj(parser.getSubject());
            agent.setBody(parser.getBody());
            String fileName = parser.getFileName();
            log.info("WriteMailHandler.sendMessage() : fileName = " + fileName); // S106
            if (fileName != null)
            {
                agent.setFile1(fileName);
            }
            // 5. 메일 전송 권한 위임
            
            if (agent.sendMessage())
            {
                status = true;
            }
        } // sendMessage()
        catch (Exception ex) {
            log.error(ex);
        }
        return status;
    }
    
    private boolean sendMessageMe(HttpServletRequest request)
    {
        boolean status = false;
        try {
            // 1. ccAddress, subject, body, file1 정보를 파싱하여 추출
            FormParser parser2 = new FormParser(request);              // 내게 메일 쓰기에는 to 정보가 없기 때문에 수정 필요함
            parser2.parse();
            HttpSession session = (HttpSession) request.getSession();
            String host = (String) session.getAttribute("host");
            String userid = (String) session.getAttribute("userid");
            
            SmtpAgent agent2 = new SmtpAgent(host, userid);
            agent2.setCc(parser2.getCcAddress());
            agent2.setSubj(parser2.getSubject());
            agent2.setBody(parser2.getBody());
            String fileName = parser2.getFileName();
            log.info("WriteMailHandler.sendMessage() : fileName = " + fileName);
            if (fileName != null)
            {
                agent2.setFile1(fileName);
            }
            // 5. 메일 전송 권한 위임
            
            if (agent2.sendMessageMe())
            {
                status = true;
            }
            
            return status;
        } // sendMessage()
        catch (Exception ex) {
            log.error(ex);
        }
        return status;
    }

    private String getMailTransportPopUp(boolean success)
    {
        String alertMessage = null;
        if (success)
        {
            alertMessage = "메일 전송 성공했습니다.";
        }
        else 
        {
            alertMessage = "메일 전송 실패했습니다.";
        }
        
        StringBuilder successPopUp = new StringBuilder();
        
        successPopUp.append("<html>");
        successPopUp.append("<head>");

        successPopUp.append("<title>메일 전송 결과</title>");
        successPopUp.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main_style.css\" />");
        successPopUp.append("</head>");
        successPopUp.append("<body onload=\"goMainMenu()\">");
        successPopUp.append("<script type=\"text/javascript\">");
        successPopUp.append("function goMainMenu() {");
        successPopUp.append("alert(\"");
        successPopUp.append(alertMessage);
        successPopUp.append("\"); ");
        successPopUp.append("window.location = \"main_menu.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
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
