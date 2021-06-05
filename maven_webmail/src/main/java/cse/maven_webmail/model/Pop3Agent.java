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
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



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
    private static final String NAME = "java:/comp/env/jdbc/fakeletter"; //S115
    
    private Log log = null;
    private List<Integer> fake_num_list = null;
    // S1068
    private static final String FOLDER_NAME = "INBOX"; // ADD JEONGEUN
    //private static Logger logger = LoggerFactory.getLogger(Pop3Agent.class);// ADD JEONGEUN

//    private boolean CONNECT_TO_STORE = connectToStore();  // ADD JEONGEUN
    
    
    public Pop3Agent() {
    }

    public Pop3Agent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
        fake_num_list = new ArrayList<>();
        log = LogFactory.getLog(Pop3Agent.class);
    }

    public boolean validate() {
        boolean status = false;

        try {
            status = connectToStore();             
            store.close();
            return status; // ADD JEONGEUN
        } catch (Exception ex) {
            //logger.trace("Pop3Agent.validate() error : " + ex); // MODIFY JEONGEUN
            log.error("Pop3Agent.validate() error : " + ex); // S106
            status = false;  // for clarity
            return status; // ADD JEONGEUN
        }
    }


    public boolean deleteMessage(int msgid, boolean reallyDelete) { // MODIFY JEONGEUN
        log.trace("======================deleteMessage===================="); // S106
        
        boolean status = false;

        if (!connectToStore()) {
            return status;
        }
        try {
            getListFromDB();
        } catch (SQLException ex) { // 
            log.error("getListFromDB() error: " + ex);
        }

        // 가져온 임시 삭제된 메시지 리스트의 msgid-1 번째 있는 메시지가 삭제할 msgid.
        // TODO
        fake_num_list.sort(Comparator.naturalOrder());
        int realMsgid = fake_num_list.get(msgid-1)+1;
        fake_num_list.remove(msgid-1);
        try {
            saveListToDB();
        } catch (SQLException ex) {
            log.error("deleteMessage() error: " + ex);
        }
        log.info(realMsgid);
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
//            logger.trace("deleteMessage() error: " + ex); // MODIFY JEONGEUN
            log.error("Pop3Agent.getMessageList() : exception = " + ex); 
            return status;
        }
    }
    
    public boolean deleteMessageFake(int msgid){
        log.trace("======================deleteMessageFake====================");
        boolean status = false;
        
        if (!connectToStore()) {
            return status;
        }
        try {
            getListFromDB();
        } catch (SQLException ex) {
            log.error(ex.getStackTrace());
        }
        
        int messageCount = fake_num_list.size();
        
        log.info("messageCount: ");
        log.info(fake_num_list.size());
        
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
            saveListToDB();
        } catch (SQLException ex){
            log.error("deleteMessageFake's saveListToDB StackTrace: ");
            log.error(ex.getStackTrace());
            log.error("Pop3Agent.getMessageList() : exception = " + ex);
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

        if (!connectToStore()) {  // 3.1
//            System.err.println("POP3 connection failed!") // MODIFY JEONGEUN
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            getListFromDB();
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder(FOLDER_NAME);  // 3.2 // ADD JEONGEUN
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            for(Message msg : messages){
                log.info("msg.getMessageNumber(): ");
                log.info(msg.getMessageNumber());
            }
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
            
            log.info("----------------------걸러낼 목록: ");
            log.info(temp);
            
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(tmp_messages, fp);
                        
            MessageFormatter formatter = new MessageFormatter(userid);  //3.5
            
            result = formatter.getMessageTable(tmp_messages);   // 3.6

            
            folder.close(true);  // 3.7
            store.close();       // 3.8
            return result; // ADD JEONGEUN
        } catch (Exception ex) {
//            System.out.println("Pop3Agent.getMessageList() : exception = " + ex); // MODIFY JEONGEUN
            log.error("Pop3Agent.getMessageList() : exception = " + ex);
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

        if (!connectToStore()) {  // 3.1
            //logger.trace("POP3 connection failed!"); // MODIFY JEONGEUN
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
            log.error("Pop3Agent.getMessageToMeList() : exception = " + ex); // MODIFY JEONGEUN
            result = "Pop3Agent.getMessageToMeList() : exception = " + ex;
            return result; // ADD JEONGEUN
        }
    }
    
    public String getMessage(int n) {
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

        if (!connectToStore()) {
            //logger.trace("POP3 connection failed!"); // MODIFY JEONGEUN
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
            log.error("Pop3Agent.getMessageList() : exception = " + ex); // MODIFY JEONGEUN
            result = "Pop3Agent.getMessage() : exception = " + ex;
            return result; // ADD JEONGEUN
        }
    }
    
    public String getTrashMessageList() throws SQLException{
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";
        
        if (!connectToStore()) {
            log.error("POP3 connection failed!");
            return result;
        }
        
        try {
            getListFromDB();
        } catch (SQLException ex) {
            log.error("Pop3Agent.getTrashMessageList() : exception = " + ex);
        }
            
        int numListCount = fake_num_list.size();       //임시로 삭제된 메일번호 목록의 개수
        log.info(numListCount);
        Message[] messages = new Message[numListCount];
        
        try{
            Folder folder = store.getFolder(FOLDER_NAME); //S1192
            folder.open(Folder.READ_ONLY);
            
            for(int i=0; i<numListCount; i++){
                log.info(fake_num_list.get(i)+1);
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
            log.error("Pop3Agent.getTrashMessageList() : exception = " + ex);
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
            return status;
        }
    }
    
    private boolean getListFromDB() throws SQLException{
        log.info("=============getListFromDB===========");
        boolean status = false;
        //TODO
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            javax.naming.Context ctx = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx.lookup(NAME);

            conn = ds.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT list FROM fakeletter WHERE userid=\'" + userid + "\'";
            log.info("sql: " + sql);
            rs = stmt.executeQuery(sql);
            
            String list="";
            while(rs.next()){
                list = rs.getString("list");
                log.info(list);
            }
            list = list.replace("[", "");
            list = list.replace("]", "");
            for(String str : list.split(", ")){
                fake_num_list.add(Integer.parseInt(str));
            }
            log.info(fake_num_list);
            status = true;

            rs.close(); // S2095
            stmt.close();
            conn.close();
        } catch(Exception e) {
            log.error(e.getStackTrace());
        }
        log.info("-----------getListFromDB--------------------");
        return status;
    }
    
    private boolean saveListToDB() throws SQLException{
        log.trace("=============saveListToDB===========");
        boolean status = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        fake_num_list.sort(Comparator.naturalOrder());
        String list = fake_num_list.toString().replace("[^0-9,]", "");
        log.info("fake_num_list: ");
        log.info(fake_num_list);
        log.info("list: " + list);
        
        try{
            javax.naming.Context ctx = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx.lookup(NAME);
            
            String sql = "UPDATE fakeletter set list=? WHERE userid=?";
            conn = ds.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, list);
            pstmt.setString(2, userid);
            log.info("sql: " + sql);
            pstmt.executeUpdate();
            status = true;
        } catch(Exception e) {
            log.error(e.getStackTrace());
        } finally {
            if(pstmt != null)
                pstmt.close();
            if(conn != null)
                conn.close();
        }
        log.info("--------------saveListToDB--------------");
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

