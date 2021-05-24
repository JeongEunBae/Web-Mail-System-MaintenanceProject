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
    private String exceptionType;
    private HttpServletRequest request;

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
        } catch (Exception ex) {
            System.out.println("Pop3Agent.validate() error : " + ex);
            status = false;  // for clarity
        } finally {
            return status;
        }
    }

    public boolean deleteMessage(int msgid, boolean really_delete) {
        boolean status = false;

        if (!connectToStore()) {
            return status;
        }

        
        // DB랑 연동해서 임시 삭제된 메시지 리스트를 가져옴
        // TODO
        
        // 가져온 임시 삭제된 메시지 리스트의 msgid-1 번째 있는 메시지가 삭제할 msgid.
        // TODO
        int realMsgid = msgid;
        
        try {
            // Folder 설정
//            Folder folder = store.getDefaultFolder();
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            // Message에 DELETED flag 설정
            Message msg = folder.getMessage(realMsgid);
            msg.setFlag(Flags.Flag.DELETED, really_delete);

            // 폴더에서 메시지 삭제
            // Message [] expungedMessage = folder.expunge();
            // <-- 현재 지원 안 되고 있음. 폴더를 close()할 때 expunge해야 함.
            folder.close(true);  // expunge == true
            store.close();
            status = true;
        } catch (Exception ex) {
            System.out.println("deleteMessage() error: " + ex);
        } finally {
            return status;
        }
    }
    
    public boolean deleteMessageFake(int msgid){
        boolean status = false;
        
        if (!connectToStore()) {
            return status;
        }
        
        if (!connectToDB()){
            return status;
        }
        
        // DB랑 연동해서 임시 삭제된 메시지 리스트를 가져옴
        // TODO
        int[] messageList = null;
        int messageCount = 0;
        
        // 가져온 메시지의 개수가 30개면 가장 앞 번호의 메시지 영구 삭제.
        // TODO
        if(messageCount == 30){
            deleteMessage(messageList[0], true);
            // 리스트에서 삭제된 메시지번호 지우기 
        }
        // 가져온 메시지 리스트에 msgid를 새로 추가하고 DB에 등록
        // TODO
        
        return status;
    }

    /*
     * 페이지 단위로 메일 목록을 보여주어야 함.
     */
    public String getMessageList() {
        String result = "";
        Message[] messages = null;

        if (!connectToStore()) {  // 3.1
            System.err.println("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder("INBOX");  // 3.2
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
        } catch (Exception ex) {
            System.out.println("Pop3Agent.getMessageList() : exception = " + ex);
            result = "Pop3Agent.getMessageList() : exception = " + ex;
        } finally {
            return result;
        }
    }

    public String getMessage(int n) {
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

        if (!connectToStore()) {
            System.err.println("POP3 connection failed!");
            return result;
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message message = folder.getMessage(n);

            MessageFormatter formatter = new MessageFormatter(userid);
            formatter.setRequest(request);  // 210308 LJM - added
            result = formatter.getMessage(message);

            folder.close(true);
            store.close();
        } catch (Exception ex) {
            System.out.println("Pop3Agent.getMessageList() : exception = " + ex);
            result = "Pop3Agent.getMessage() : exception = " + ex;
        } finally {
            return result;
        }
    }
    
    public String getTrashMessageList(){
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";
        int[] numList = {1, 3};     //DB로 가져온 임시로 삭제된 메일번호 목록
        int numListCount = 2;       //임시로 삭제된 메일번호 목록의 개수
        
        if (!connectToStore()) {
            System.err.println("POP3 connection failed!");
            return result;
        }
        
        Message[] messages = new Message[numListCount];
        
        try{
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            for(int i=0; i<numListCount; i++){
                messages[i] = folder.getMessage(numList[i]);
            }

            MessageFormatter formatter = new MessageFormatter(userid);  //3.5
            result = formatter.getTempMessageTable(messages);   // 3.6
            
            folder.close(true);  // 3.7
            store.close();       // 3.8
        } catch(Exception ex){
            System.out.println("Pop3Agent.getTrashMessageList() : exception = " + ex);
            result = "Pop3Agent.getTrashMessageList() : exception = " + ex;
        }
        return result;
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
        } catch (Exception ex) {
            exceptionType = ex.toString();
        } finally {
            return status;
        }
    }
    
    private boolean connectToDB(){
        boolean status = false;
        //TODO
        return status;
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

