/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychatserver;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 *
 * @author User
 */
public class MyChatServer {

    public final static int port=7000;
    public final static int port2=7700;
    public static String getString(){
        String S="";
        try{
            LineNumberReader LNR=new LineNumberReader(new InputStreamReader(System.in,"CP1251"));
            S=LNR.readLine();
        }catch(Exception ex){
            S="";
        }
        return S;
        
    }
    public static int getInt(){
        try{
            return Integer.parseInt(getString());
        }catch(Exception ex){
            return 0;
        }
    }
    public static void main(String[] args) {
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println("Driver loading success!");            
            
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println(cnfe.getMessage());
            System.exit(0);
        }
        catch (InstantiationException | IllegalAccessException ie) {
            System.out.println(ie.getMessage());
        } 
        
        ServerThread thr=new ServerThread("192.168.0.102", port,port2);
        thr.setDaemon(true);
        thr.start();
        int n=getInt();
    }
    
}
