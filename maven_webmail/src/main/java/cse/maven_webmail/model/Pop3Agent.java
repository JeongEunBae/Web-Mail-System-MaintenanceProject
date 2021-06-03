/*
 * File: Pop3Agent.java
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import java.util.Properties;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author jongmin
 */
public class Pop3Agent {

    private String host;
    private String userid;
    private String password;
    private Store store;
    private HttpServletRequest request;

    private static final String FOLDER_NAME = "INBOX"; // ADD JEONGEUN
    private static final Logger log = Logger.getGlobal();  // ADD JEONGEUN

    private static final boolean CONNECT_TO_STORE = connectToStore();  // ADD JEONGEUN
    public Pop3Agent() {
    }

    public Pop3Agent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
    }

    public boolean validate() {
        boolean status = false;

        try {
            status = connectToStore();             
            store.close();
            return status; // ADD JEONGEUN
        } catch (Exception ex) {
            log.info("Pop3Agent.validate() error : " + ex); // MODIFY JEONGEUN
            status = false;  // for clarity
            return status; // ADD JEONGEUN
        }
    }

    public boolean deleteMessage(int msgid, boolean reallyDelete) { // MODIFY JEONGEUN
        boolean status = false;

        if (!CONNECT_TO_STORE) {
            return status;
        }

        try {
            // Folder 설정
            Folder folder = store.getFolder(FOLDER_NAME); // ADD JEONGEUN
            folder.open(Folder.READ_WRITE);

            // Message에 DELETED flag 설정
            Message msg = folder.getMessage(msgid);
            msg.setFlag(Flags.Flag.DELETED, reallyDelete); // MODIFY JEONGEUN

            // 폴더에서 메시지 삭제
           
            // <-- 현재 지원 안 되고 있음. 폴더를 close()할 때 expunge해야 함.
            folder.close(true);  // expunge == true
            store.close();
            status = true;
            return status; // ADD JEONGEUN
        } catch (Exception ex) {
            log.info("deleteMessage() error: " + ex); // MODIFY JEONGEUN
        }
    }

    /*
     * 페이지 단위로 메일 목록을 보여주어야 함.
     */
    public String getMessageList() {
        String result = "";
        Message[] messages = null;

        if (!CONNECT_TO_STORE()) {  // 3.1
//            System.err.println("POP3 connection failed!") // MODIFY JEONGEUN
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder(FOLDER_NAME);  // 3.2 // ADD JEONGEUN
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);

            MessageFormatter formatter = new MessageFormatter(userid);  //3.5
            result = formatter.getMessageTable(messages);   // 3.6
            
            folder.close(true);  // 3.7
            store.close();       // 3.8
            return result; // ADD JEONGEUN
        } catch (Exception ex) {
//            System.out.println("Pop3Agent.getMessageList() : exception = " + ex); // MODIFY JEONGEUN
            result = "Pop3Agent.getMessageList() : exception = " + ex;
            return result; // ADD JEONGEUN
        }
    }
    
        /*
     * 페이지 단위로 나에게 온 메일 목록을 보여주어야 함.
     */
    public String getMessageToMeList() { // ADD JEONGEUN
        String result = "";
        Message[] messages = null;

        if (!CONNECT_TO_STORE()) {  // 3.1
            log.info("POP3 connection failed!"); // MODIFY JEONGEUN
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder(FOLDER_NAME);  // 3.2 // ADD JEONGEUN
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);
            MessageFormatter formatter = new MessageFormatter(userid);  //3.5
            result = formatter.getMessageToMeTable(messages);   // 3.6 
            
            folder.close(true);  // 3.7
            store.close();       // 3.8
            return result; // ADD JEONGEUN
        } catch (Exception ex) {
            log.info("Pop3Agent.getMessageToMeList() : exception = " + ex); // MODIFY JEONGEUN
            result = "Pop3Agent.getMessageToMeList() : exception = " + ex;
            return result; // ADD JEONGEUN
        }
    }
    
    public String getMessage(int n) {
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

        if (!CONNECT_TO_STORE()) {
            log.info("POP3 connection failed!"); // MODIFY JEONGEUN
            return result;
        }

        try {
            Folder folder = store.getFolder(FOLDER_NAME); // ADD JEONGEUN
            folder.open(Folder.READ_ONLY);

            Message message = folder.getMessage(n);

            MessageFormatter formatter = new MessageFormatter(userid);
            formatter.setRequest(request);  // 210308 LJM - added
            result = formatter.getMessage(message); 

            folder.close(true);
            store.close();
            return result; // ADD JEONGEUN
        } catch (Exception ex) {
            log.info("Pop3Agent.getMessageList() : exception = " + ex); // MODIFY JEONGEUN
            result = "Pop3Agent.getMessage() : exception = " + ex;
            return result; // ADD JEONGEUN
        }
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
            return status; // ADD JEONGEUN
        } catch (Exception ex) {
            exceptionType = ex.toString();
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
    
    
}  // class Pop3Agent

