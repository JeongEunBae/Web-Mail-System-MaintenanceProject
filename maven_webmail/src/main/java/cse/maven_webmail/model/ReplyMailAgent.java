/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

// S1128
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author byg09
 */
public class ReplyMailAgent {
    private String host;
    private String userid;
    private String password; 
    private String toAddress; //S1068
    private String fromAddress;
    private String ccAddress;
    private String sentDate;
    private String subject;
    private String body;
    private String fileName;
    private int replyNo;
    // S1068
    private Store store;
    private HttpServletRequest request;

    public ReplyMailAgent() {
    }

    
    public ReplyMailAgent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
    }
    
    public String getUserid() {
        return userid;
    }

    public String getToAddress() {
        return toAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getCcAddress() {
        return ccAddress;
    }

    public String getSentDate() {
        return sentDate;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getFileName() {
        return fileName;
    }

    public int getReplyNo() { // S100
        return replyNo;
    }

    public void saveMessage(int n) {
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

        if (!connectToStore()) {
            out.println(result); // S106
            return;
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message message = folder.getMessage(n);

            MessageFormatter formatter = new MessageFormatter(userid);
            formatter.setRequest(request);  
            
            MessageParser parser = new MessageParser(message, userid, request);
            parser.parse(true);
            
            this.fromAddress = parser.getFromAddress();
            this.toAddress = parser.getToAddress();
            this.ccAddress = parser.getCcAddress();
            this.subject = parser.getSubject();
            this.body = parser.getBody();
            this.sentDate = parser.getSentDate();
            this.fileName = parser.getFileName();
            this.replyNo = n;
            
            folder.close(true);
            store.close();
        } catch (Exception ex) {
            out.println("Pop3Agent.getMessageList() : exception = " + ex); // S106
        }
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    private boolean connectToStore() {
        boolean status = false;
        Properties props = System.getProperties();
        props.setProperty("mail.pop3.host", host);
        props.setProperty("mail.pop3.user", userid);
        props.setProperty("mail.pop3.apop.enable", "false");
        props.setProperty("mail.pop3.disablecapa", "true");  // 200102 LJM - added cf. https://javaee.github.io/javamail/docs/api/com/sun/mail/pop3/package-summary.html
        props.setProperty("mail.debug", "true");

        Session session = Session.getInstance(props);
        session.setDebug(true);

        try {
            store = session.getStore("pop3");
            store.connect(host, userid, password);
            status = true;
            return status; // S1143
        } catch (Exception ex) {
            return status;
        }
    }
}
