/*
 * File: Pop3Agent.java
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import java.util.Properties;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.naming.NamingException;
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
    private String username;
    private final static String name = "java:/comp/env/jdbc/fakeletter";
    private List<Integer> fake_num_list = new ArrayList<>();
    private int message_count;
    

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

<<<<<<< HEAD
    public boolean deleteMessage(int msgid, boolean reallyDelete) { // MODIFY JEONGEUN
=======
    public boolean deleteMessage(int msgid, boolean really_delete) {
        System.out.println("======================deleteMessage====================");
>>>>>>> feature/MailTempDelete
        boolean status = false;

        if (!CONNECT_TO_STORE) {
            return status;
        }
        try {
            if(!getListFromDB()){
                System.out.println("deleteMessage DB");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Pop3Agent.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 가져온 임시 삭제된 메시지 리스트의 msgid-1 번째 있는 메시지가 삭제할 msgid.
        // TODO
        fake_num_list.sort(Comparator.naturalOrder());
        int realMsgid = fake_num_list.get(msgid-1)+1;
        fake_num_list.remove(msgid-1);
        try {
            saveListToDB();
        } catch (SQLException ex) {
            Logger.getLogger(Pop3Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(realMsgid);
        try {
            // Folder 설정
            Folder folder = store.getFolder(FOLDER_NAME); // ADD JEONGEUN
            folder.open(Folder.READ_WRITE);

            // Message에 DELETED flag 설정
<<<<<<< HEAD
            Message msg = folder.getMessage(msgid);
            msg.setFlag(Flags.Flag.DELETED, reallyDelete); // MODIFY JEONGEUN
=======
            Message msg = folder.getMessage(realMsgid);
            msg.setFlag(Flags.Flag.DELETED, really_delete);
>>>>>>> feature/MailTempDelete

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
    
    public boolean deleteMessageFake(int msgid){
        System.out.println("======================deleteMessageFake====================");
        boolean status = false;
        
        if (!connectToStore()) {
            return status;
        }
        try {
            if (!getListFromDB()){
                System.out.println("db 오류");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getStackTrace());
            Logger.getLogger(Pop3Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int messageCount = fake_num_list.size();
        
        System.out.print("messageCount: ");
        System.out.println(fake_num_list.size());
        
        // 가져온 메시지의 개수가 30개면 가장 앞 번호의 메시지 영구 삭제.
        // TODO
        if(messageCount == 30){
            deleteMessage(fake_num_list.get(0), true);
            fake_num_list.remove(0);
            fake_num_list.add(msgid-1);
            // 리스트에서 삭제된 메시지번호 지우기 
        }
        // 가져온 메시지 리스트에 msgid를 새로 추가하고 DB에 등록
        // TODO
        int count = 0;
        for(int num : fake_num_list){
            if(msgid+messageCount > num){
                count++;
            }
        }
        fake_num_list.add(msgid+count-1);
        try{
            if(!saveListToDB()){
                System.out.println("db 오류");
            }
        } catch (SQLException ex){
            System.out.println("deleteMessageFake's saveListToDB StackTrace: ");
            System.out.println(ex.getStackTrace());
            Logger.getLogger(Pop3Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
        status = true;
        return status;
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
            if(!getListFromDB()){
                System.out.println("DB연결 오류");
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder(FOLDER_NAME);  // 3.2 // ADD JEONGEUN
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            
            for(Message msg : messages){
                System.out.print("msg.getMessageNumber(): ");
                System.out.println(msg.getMessageNumber());
            }
            message_count = messages.length;
            int size = messages.length;
            Message[] tmp_messages = new Message[size-fake_num_list.size()];
            
            // 수신한 메시지 중에서 임시로 삭제된 메시지 걸러내기
            ArrayList<Integer> temp = new ArrayList<>();
            if(fake_num_list.size() > 0){
                temp.addAll(fake_num_list);
                temp.sort(Comparator.reverseOrder());
                for(int index : temp){
                    
                    messages[index] = null;
                }
            }
            for(int i=0, j=0; i<size; i++){
                if(messages[i] != null){
                    tmp_messages[j++] = messages[i];
                }
            }
            
            System.out.print("----------------------걸러낼 목록: ");
            System.out.println(temp);
            
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(tmp_messages, fp);
                        
            MessageFormatter formatter = new MessageFormatter(userid);  //3.5
<<<<<<< HEAD
            result = formatter.getMessageTable(messages);   // 3.6
=======
            result = formatter.getMessageTable(tmp_messages);   // 3.6
>>>>>>> feature/MailTempDelete
            
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
<<<<<<< HEAD
            result = formatter.getMessage(message); 

=======
            result = formatter.getMessage(message);
            
>>>>>>> feature/MailTempDelete
            folder.close(true);
            store.close();
            return result; // ADD JEONGEUN
        } catch (Exception ex) {
            log.info("Pop3Agent.getMessageList() : exception = " + ex); // MODIFY JEONGEUN
            result = "Pop3Agent.getMessage() : exception = " + ex;
            return result; // ADD JEONGEUN
        }
    }
    
    public String getTrashMessageList(){
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";
        
        if (!connectToStore()) {
            System.err.println("POP3 connection failed!");
            return result;
        }
        
        try {
            if(!getListFromDB()){
                return "DB 서버 연결이 되지 않아 메시지를 볼 수 없습니다.";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Pop3Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        int numListCount = fake_num_list.size();       //임시로 삭제된 메일번호 목록의 개수
        System.out.println(numListCount);
        Message[] messages = new Message[numListCount];
        
        try{
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            
            for(int i=0; i<numListCount; i++){
                System.out.println(fake_num_list.get(i)+1);
                messages[i] = folder.getMessage(fake_num_list.get(i)+1);
            }
            
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);
            
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
            return status; // ADD JEONGEUN
        } catch (Exception ex) {
            exceptionType = ex.toString();
        }
    }
    
    private boolean getListFromDB() throws SQLException{
        System.out.println("=============getListFromDB===========");
        boolean status = false;
        //TODO
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            javax.naming.Context ctx = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx.lookup(name);

            conn = ds.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT list FROM fakeletter WHERE id=\'" + userid + "\'";
            System.out.println("sql: " + sql);
            rs = stmt.executeQuery(sql);
            
            String list="";
            while(rs.next()){
                list = rs.getString("list");
                System.out.println(list);
            }
            list = list.replace("[", "");
            list = list.replace("]", "");
            for(String str : list.split(", ")){
                System.out.println(str);
                fake_num_list.add(Integer.parseInt(str));
            }
            System.out.println(fake_num_list);
            status = true;
        } catch(Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            if(conn != null)
                conn.close();
        }
        
        System.out.println("-----------getListFromDB--------------------");
        return status;
    }
    
    private boolean saveListToDB() throws SQLException{
        System.out.println("=============saveListToDB===========");
        boolean status = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        fake_num_list.sort(Comparator.naturalOrder());
        String list = fake_num_list.toString().replace("[^0-9,]", "");
        System.out.print("fake_num_list: ");
        System.out.println(fake_num_list);
        System.out.println("list: " + list);
        
        
        try{
            javax.naming.Context ctx = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx.lookup(name);
            
            String sql = "UPDATE fakeletter set list=? WHERE id=?";
            conn = ds.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, list);
            pstmt.setString(2, userid);
            System.out.println("sql: " + sql);
            pstmt.executeUpdate();
            status = true;
        } catch(Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            if(pstmt != null)
                pstmt.close();
            if(conn != null)
                conn.close();
        }
        System.out.println("--------------saveListToDB--------------");
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

