/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author chung
 */
public class ResetPasswordAgent extends UserAdminAgent {
    private Socket socket = null;
    private InputStream is = null;
    private OutputStream os = null;
    private boolean isConnected = false;
    private static final String EOL = "\r\n";
    Log log = null;
    
    public ResetPasswordAgent(String server, int port, String cwd){
        super(server, port, cwd);
        log = LogFactory.getLog(ResetPasswordAgent.class);
        log.info("ResetPasswordAgent create: server= " + server + ", port= " + port);
        isConnected = super.isIsConnected();
        socket = super.getSocket();
        is = socket.getInputStream();
        os = socket.getOutputStream();
    }
    
    public boolean resetPasswd(String userId, String passwd){
        boolean status = false;
        byte[] messageBuffer = new byte[1024];
        
        log.info("resetPasswd() called");
        if(!isConnected){
            return status;
        }
        
        try{
            // 1: "setpassword" command
            String setPasswordCommand = "setpassword " + userId + " " + passwd + EOL;
            os.write(setPasswordCommand.getBytes());
            
            // 2: response for "setpassword" command
            java.util.Arrays.fill(messageBuffer, (byte) 0);
            
            String recvMessage = "";
            if(is.read(messageBuffer) > 0){
                recvMessage = new String(messageBuffer);
                log.info(recvMessage);
            }
            
            if(recvMessage.contains("Password for " + userId + " reset")){
                status = true;
            } else {
                status = false;
            }
            
            quit();
            socket.close();
        } catch (Exception ex){
            log.error(ex);
            status = false;
        }
        return status;
    }
}