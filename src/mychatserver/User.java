/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychatserver;

import java.net.Socket;

/**
 *
 * @author User
 */
public class User {
    private String name;
    private int hash;
    private Socket socket;
    private Socket socket2;

    public User(String name, int hash, Socket socket, Socket socket2) {
        this.name = name;
        this.hash = hash;
        this.socket = socket;
        this.socket2=socket2;
    }

    public Socket getSocket2() {
        return socket2;
    }

    public void setSocket2(Socket socket2) {
        this.socket2 = socket2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    
}
