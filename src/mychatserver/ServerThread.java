/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychatserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class ServerThread extends Thread{
    
    private String ipAddress;
    private int port;
    private int port2;
    private RegularSendingThread regThr;
    public static ArrayList<User> users;
    public static ArrayList<String>messages;
    private Connection conection=null;
    private Properties props=null;
    private String url="";
    //public static TreeMap<String,Socket> users; 
    
    public ServerThread(String ipAddress, int port, int port2){
        this.ipAddress=ipAddress;
        this.port=port;
        this.port2=port2;
        //users=new TreeMap<String,Socket>();
        users=new ArrayList<User>();
        messages=new ArrayList<String>();
        regThr=new RegularSendingThread();
    }
    
    @Override
    public void run(){
        try{
            ServerSocket server=new ServerSocket(port,50,InetAddress.getByName(ipAddress));
            ServerSocket server2=new ServerSocket(port2,50,InetAddress.getByName(ipAddress));
            ByteArrayOutputStream BAOS=new ByteArrayOutputStream();
            while(true){
                try {

                    System.out.println("Waiting to connection");
                    Socket client=server.accept();
                    System.out.println("Connection succesfully ");
                    
                    byte[] buf=new byte[16];
                    BAOS.reset();
                
                     do {
                        int cnt=client.getInputStream().read(buf,0,buf.length);
                        if(cnt==-1) throw new IOException("reseived -1");//socket closed
                        BAOS.write(buf,0,buf.length);
                    } while (client.getInputStream().available()>0);
               
                
                        byte[]MSG=BAOS.toByteArray(); 

                        int length=0; 
                        String nick="";
                        String password="";
                        String message="";
                        String act="";
                        String state="";

                        ByteArrayInputStream BAIS=new ByteArrayInputStream(MSG);
                        DataInputStream DIS=new DataInputStream(BAIS);
                        try{
                            length=DIS.readInt();
                            System.out.println("length : "+length);
                            act=DIS.readUTF();
                            System.out.println("act : "+act);
                            nick=DIS.readUTF();     
                            System.out.println("nick : "+nick);
                            password=DIS.readUTF();
                            System.out.println("password : "+password);
                            DIS.close();
                        }catch(IOException ex){
                            System.out.println("Exception1 : "+ex.getMessage());
                        }
                        
                    int resUser=isExist(nick,password, client);
                    
                        if (resUser==3) {                                   
                            
                            state="NICKOK";
                            message=String.valueOf(MyChatServer.port2);
                            
                            BAOS.reset();
                            DataOutputStream DOS=new DataOutputStream(BAOS);
                            
                            try{
                                DOS.writeUTF(state);
                                DOS.writeUTF(message);
                            }catch(IOException ex){
                                System.out.println("Exception : "+ex.getMessage());
                            }                              
                            
                            client.getOutputStream().write(BAOS.toByteArray());
                            
                            //отправляем и ждем нового подключения по второму порту
                            
                            System.out.println("Waiting to connection2");
                            Socket client2=server2.accept();
                            System.out.println("Connection succesfully2 ");
                            ServerThread.users.add(new User(message,client.hashCode(),client,client2));
                            
                            ReadWriterThread thr=new ReadWriterThread(client,nick);
                            thr.setDaemon(true);
                            thr.start();
                            
                            if (!regThr.isAlive()) {
                                regThr.setDaemon(true);
                                regThr.start();
                            }
                        
                    }
                        else {
                            if (resUser==0) {
                                state="NICKERROR";
                                message="User already exsist";
                            }
                            else if(resUser==1)
                            {
                                state="NICKERROR";
                                message="User already in a chat";
                            }
                            else if(resUser==2){
                                state="NICKERROR";
                                message="Wrong password";
                            }
                            
                            BAOS.reset();
                            DataOutputStream DOS=new DataOutputStream(BAOS);
                            
                            try{
                                DOS.writeUTF(state);
                                DOS.writeUTF(message);
                            }catch(IOException ex){
                                System.out.println("Exception : "+ex.getMessage());
                            }                              
                            
                            client.getOutputStream().write(BAOS.toByteArray());
                        }
                    
                    
                    

                } catch (IOException ex) {
                    System.out.println("Ошибка получения запроса на установление соединения : "+ex.getMessage());
                    break;
                }
            }
        }catch(IOException ex){
            System.out.println("Exception : "+ex.getMessage());
        }        
    }
    
    public int isExist(String name,String password, Socket client){                
            
        
        String state="";
        synchronized(ServerThread.users){
            for(User user: ServerThread.users){
                if (user.getName().equals(name)) {
                    return 0;
                }
                else
                if (user.getHash()==client.hashCode()) {
                    return 1;
                }
            }
            
        }                 
       
        
        
        this.props=new Properties();
        props.put("user", "root");
        props.put("password", "");
        props.put("useUnicode", "true");
        props.put("characterEncoding", "utf8");
        
        props.put("autoReconnect", "false"); // признак автоматического переподключения : false - вручную
        props.put("maxReconnects", "3");     // Максимальное кол-во переподключений
        
        
        url="jdbc:mysql://localhost:3306/Users";
        
        try{
            this.conection=DriverManager.getConnection(url,props);
        }catch(SQLException ex){
            System.out.println("Ошибка подключения к СУБД1 : " + ex.getMessage());
            //System.exit(0);
            
        }
        String pass="";
        try{
            String query=( "SELECT * FROM users WHERE user_name = \'Den\' ");
            System.out.println(query);
            
            ResultSet result =this.conection.createStatement().executeQuery(query);
            while(result.next()){
                pass=result.getString("user_password");
            }
            
            System.out.println("password : "+password);
            System.out.println("passwordDB : "+pass);
        }catch(SQLException ex){
            System.out.println("Ошибка подключения к СУБД2 : " + ex.getMessage());
            //System.exit(0);
            
        }
        if (password.equals(pass)) {
            return 3;
        }
        else
            return 2;
        
        
    }
    
}
