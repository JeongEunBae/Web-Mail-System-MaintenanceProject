/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author chung
 */
public class ResetPasswordAgent extends UserAdminAgent {
    private String server;
    private int port;
    private Socket socket = null;
    private InputStream is = null;
    private OutputStream os = null;
    private boolean isConnected = false;
    private final String EOL = "\r\n";
    
    public ResetPasswordAgent(String server, int port, String cwd) throws Exception{
        super(server, port, cwd);
        System.out.println("ResetPasswordAgent create: server= " + server + ", port= " + port);
//        this.server = server;
//        this.port = port;
//        this.cwd = cwd;
//        this.ROOT_ID = super.getROOT_ID();
//        this.ROOT_PASSWORD = super.getROOT_PASSWORD();
//        this.ADMIN_ID = super.getADMIN_ID();
        isConnected = super.isIsConnected();
        socket = super.getSocket();
        is = socket.getInputStream();
        os = socket.getOutputStream();
//        this.is = 
    }
    
    public boolean resetPasswd(String userId, String passwd){
        boolean status = false;
        byte[] messageBuffer = new byte[1024];
        
        System.out.println("resetPasswd() called");
        if(!isConnected){
            return status;
        }
        
        try{
            // 1: "setpassword" command
            String setPasswordCommand = "setpassword" + userId + " " + passwd + EOL;
            os.write(setPasswordCommand.getBytes());
            
            // 2: response for "setpassword" command
            java.util.Arrays.fill(messageBuffer, (byte) 0);
            
            is.read(messageBuffer);
            String recvMessage = new String(messageBuffer);
            System.out.println(recvMessage);
            
            if(recvMessage.contains("Password for " + userId + " reset")){
                status = true;
            } else {
                status = false;
            }
            
            quit();
            System.out.flush();
            socket.close();
        } catch (Exception ex){
            System.out.println(ex.toString());
            status = false;
        } finally {
            return status;
        }
    }
}
