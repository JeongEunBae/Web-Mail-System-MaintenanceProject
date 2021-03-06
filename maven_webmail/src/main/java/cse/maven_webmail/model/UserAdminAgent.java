/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.System.out;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jongmin
 */
public class UserAdminAgent {

    private String server;
    private int port;
    Socket socket = null;
    InputStream is = null;
    OutputStream os = null;
    boolean isConnected = false;
    private String ROOT_ID;
    private String ROOT_PASSWORD;
    private String ADMIN_ID;
    private static final String EOL = "\r\n";
    String cwd;
    Log log = LogFactory.getLog(UserAdminAgent.class);

    public UserAdminAgent(String server, int port, String cwd) throws Exception {

        log.info("UserAdminAgent created: server = " + server + ", port = " + port);
        this.server = server;  // 127.0.0.1
        this.port = port;  // 4555
        this.cwd = cwd;

        initialize();

        socket = new Socket(server, port);
        is = socket.getInputStream();
        os = socket.getOutputStream();

        isConnected = connect();
    }

    private void initialize() {
        // property 읽는 방법 맞는지? getClass().getResourceAsStream() 사용해 보면...
        Properties props = new Properties();
        String propertyFile = this.cwd + "/WEB-INF/classes/config/system.properties";
        propertyFile = propertyFile.replace("\\", "/");
        log.info("prop path = " + propertyFile); // S106

        try (BufferedInputStream bis
                = new BufferedInputStream(
                        new FileInputStream(propertyFile))) {
            props.load(bis);
            ROOT_ID = props.getProperty("root_id");
            ROOT_PASSWORD = props.getProperty("root_password");
            ADMIN_ID = props.getProperty("admin_id");
            log.info(String.format("ROOT_ID = %s\nROOT_PASS = %s\n", ROOT_ID, ROOT_PASSWORD)); // S106 , 3457
        } catch (IOException ioe) {
            log.error("UserAdminAgent: 초기화 실패 - " + ioe.getMessage());
        }

    }

    // return value:
    //   - true: addUser operation successful
    //   - false: addUser operation failed
    public boolean addUser(String userId, String password) {
        boolean status = false;
        String recvMessage = "";
        byte[] messageBuffer = new byte[1024];

        log.info("addUser() called");
        if (!isConnected) {
            return status;
        }

        try {
            // 1: "adduser" command
            String addUserCommand = "adduser " + userId + " " + password + EOL;
            os.write(addUserCommand.getBytes());

            // 2: response for "adduser" command
            java.util.Arrays.fill(messageBuffer, (byte) 0);

            int count = 0;
            is.read(messageBuffer);
            recvMessage = new String(messageBuffer);
            log.info(recvMessage);

            // 3: 기존 메일사용자 여부 확인
            if (recvMessage.contains("added")) {
                status = true;
            } else {
                status = false;
            }
            // 4: 연결 종료
            quit();
            out.flush();// for test // S106
            socket.close();
        } catch (Exception ex) {
            log.error(ex.toString());
            status = false;
        } finally {
            return status;
        }
    }

    public List<String> getUserList() {
        List<String> userList = new LinkedList<>();
        byte[] messageBuffer = new byte[1024];

        if (!isConnected) {
            return userList;
        }

        try {
            // 1: "listusers" 명령 송신
            String command = "listusers " + EOL;
            os.write(command.getBytes());

            // 2: "listusers" 명령에 대한 응답 수신
            java.util.Arrays.fill(messageBuffer, (byte) 0);
            is.read(messageBuffer);
            
            // 3: 응답 메시지 처리
            String recvMessage = new String(messageBuffer);
            log.info(recvMessage);
            userList = parseUserList(recvMessage);

            quit();
            return userList; // S1143
        } catch (Exception ex) {
            log.error(ex);
            return userList;
        }
    }  // getUserList()

    private List<String> parseUserList(String message) {
        List<String> userList = new LinkedList<>();

        // 1: 줄 단위로 나누기
        String[] lines = message.split(EOL);
        log.info(lines);
        // 2: 첫 번째 줄에는 등록된 사용자 수에 대한 정보가 있음.
        //    예) Existing accounts 7
        String[] firstLine = lines[0].split(" ");
        int numberOfUsers = Integer.parseInt(firstLine[2]);

        // 3: 두 번째 줄부터는 각 사용자 ID 정보를 보여줌.
        //    예) user: admin
        for (int i = 1; i <= numberOfUsers; i++) {
            // 3.1: 한 줄을 구분자 " "로 나눔.
            String[] userLine = lines[i].split(" ");
            // 3.2 사용자 ID가 관리자 ID와 일치하는 지 여부 확인
            if (!userLine[1].equals(ADMIN_ID)) {
                userList.add(userLine[1]);
            }
        }
        return userList;
    } // parseUserList()

    public boolean deleteUsers(String[] userList) {
        byte[] messageBuffer = new byte[1024];
        String command;
        String recvMessage;
        boolean status = false;

        if (!isConnected) {
            return status;
        }

        try {
            for (String userId : userList) {
                // 1: "deluser" 명령 송신
                command = "deluser " + userId + EOL;

                os.write(command.getBytes());

                log.info(command);
                // 2: 응답 메시지 수신
                java.util.Arrays.fill(messageBuffer, (byte) 0);
                is.read(messageBuffer);

                // 3: 응답 메시지 분석
                recvMessage = new String(messageBuffer);
                log.info(recvMessage);
                if (recvMessage.contains("deleted")) {
                    status = true;
                }
                quit();
            }

            return status; // S1143
        } catch (Exception ex) {
            log.error(ex);
            return status;
        }
    }  // deleteUsers()

    public boolean verify(String userid) {
        boolean status = false;
        byte[] messageBuffer = new byte[1024];

        try {
            // --> verify userid
            String verifyCommand = "verify " + userid;
            os.write(verifyCommand.getBytes());

            // read the result for verify command
            // <-- User userid exists   or
            // <-- User userid does not exist
            int readCount = 0;
            while ((readCount = is.read(messageBuffer)) > 0) { // S2674
                String recvMessage = new String(messageBuffer);
                if (recvMessage.contains("exists")) {
                    status = true;
                    quit();  // quit command
                }
            }

            return status; // S1143
        } catch (IOException ex) {
            log.error(ex);
            return status;
        }
    }
    private boolean connect() throws Exception {
        byte[] messageBuffer = new byte[1024];
        boolean returnVal = false;
        String sendMessage;

        log.info("UserAdminAgent.connect() called...");

        // root 인증: id, passwd - default: root
        // 1: Login Id message 수신
        is.read(messageBuffer);
        String recvMessage = new String(messageBuffer);
        log.info("recvMessage: " + recvMessage);

        // 2: rootId 송신
        sendMessage = ROOT_ID + EOL;
        os.write(sendMessage.getBytes());

        // 3: Password message 수신
        java.util.Arrays.fill(messageBuffer, (byte) 0);
        is.read(messageBuffer);
        recvMessage = new String(messageBuffer);
        log.info("recvMessage: " + recvMessage);

        // 4: rootPassword 송신
        sendMessage = ROOT_PASSWORD + EOL;
        os.write(sendMessage.getBytes());

        // 5: welcome message 수신
        java.util.Arrays.fill(messageBuffer, (byte) 0);
        is.read(messageBuffer);
        recvMessage = new String(messageBuffer);
        log.info("recvMessage: " + recvMessage);

        if (recvMessage.contains("Welcome")) {
            returnVal = true;
        } else {
            returnVal = false;
        }
        return returnVal;
    }  // connect()

    public boolean quit() {
        byte[] messageBuffer = new byte[1024];
        boolean status = false;
        // quit
        try {
            // 1: quit 명령 송신
            String quitCommand = "quit" + EOL;
            os.write(quitCommand.getBytes());
            // 2: quit 명령에 대한 응답 수신
            java.util.Arrays.fill(messageBuffer, (byte) 0);
            if (is.available() > 0) {
                is.read(messageBuffer);
            }
            // 3: 메시지 분석
            String recvMessage = new String(messageBuffer);
            log.info(recvMessage);
            if (recvMessage.contains("closed")) {
                status = true;
            } else {
                status = false;
            }
            return status; // S1143

        } catch (IOException ex) {
            log.error("UserAdminAgent.quit() " + ex);
            return status; // S1143
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getIs() {
        return is;
    }

    public OutputStream getOs() {
        return os;
    }

    public boolean isIsConnected() {
        return isConnected;
    }

    public String getROOT_ID() {
        return ROOT_ID;
    }

    public String getROOT_PASSWORD() {
        return ROOT_PASSWORD;
    }

    public String getADMIN_ID() {
        return ADMIN_ID;
    }

}
