/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import java.util.LinkedList;
import java.util.List;
import cse.maven_webmail.model.UserAdminAgent;
/**
 *
 * @author 김기목
 */
public class UserAgent {
    private String server;
    private int port;
    private String cwd;

    private UserAdminAgent agent;
    List<String> userList = new LinkedList<String>();
    
    public UserAgent(){
        
    }

    public List<String> getUserList() throws Exception{
        agent = new UserAdminAgent("localhost", 4555, cwd);
        return agent.getUserList();
    }
    
    
    public String getCwd() {
        return cwd;
    }

    public void setCwd(String cwd) {
        this.cwd = cwd;
    }
    
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}