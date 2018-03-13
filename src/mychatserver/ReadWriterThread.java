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
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author User
 */
public class ReadWriterThread extends Thread{
    private Socket client;
    private String nick="";
    
    public ReadWriterThread(Socket client, String nick){
        this.client=client;
        this.nick=nick;
    }
    
    @Override
    public void run(){
        try{
            byte[] buf=new byte[16];
            ByteArrayOutputStream BAOS=new ByteArrayOutputStream();
            while(true){
                BAOS.reset();
                
                     do {
                        int cnt=this.client.getInputStream().read(buf,0,buf.length);
                        if(cnt==-1) throw new IOException("reseived -1");//socket closed
                        BAOS.write(buf,0,buf.length);
                    } while (this.client.getInputStream().available()>0);
               
                
                byte[]MSG=BAOS.toByteArray(); 

                int length=0; 
                String message="";
                String message2="";
                String act="";
                String state="";
                
                ByteArrayInputStream BAIS=new ByteArrayInputStream(MSG);
                DataInputStream DIS=new DataInputStream(BAIS);
                try{
                    length=DIS.readInt();
                    //System.out.println("length : "+length);
                    act=DIS.readUTF();
                    //System.out.println("act : "+act);
                    message=DIS.readUTF();     
                    //System.out.println("message : "+message);
                    DIS.close();
                }catch(IOException ex){
                    System.out.println("Exception1 : "+ex.getMessage());
                }
                

                switch(act)
                {
//                    case "NICK":                     
//                            synchronized(ServerThread.users){
//                                for(User user: ServerThread.users){
//                                    if (user.getName().equals(message)) {
//                                        state="NICKERROR";
//                                        message="User with this nick is already exist";                                    
//                                        break;
//                                    }
//                                    else
//                                    if (user.getHash()==client.hashCode()) {
//                                        state="NICKERROR";
//                                        message="User already have nick";                                    
//                                        break;
//                                    }
//                                }
//                                 if (!act.equals("NICKERROR")) {
//                                    //ServerThread.users.add(new User(message,client.hashCode(),client));
//                                    this.nick=message;
//                                    state="NICKOK";
//                                    message=String.valueOf(MyChatServer.port2);
//                                }
//                            }                        
//                       
//                        break;
                    case "SMS":
                        
//                        if (!nick.equals("")) {
//                            synchronized(ServerThread.messages){
//                                ServerThread.messages.add(nick+" : "+message);
//                            }                            
//                            state="SMSOK";
//                            message="sdsadasd";                            
//                            
//                        }else{
//                            state="SMSERROR";
//                            message="User not authorized yet";
//                        }
                        message2=nick+" : "+message;
                        synchronized(ServerThread.messages){
                                ServerThread.messages.add(message2);
                            }                            
                            state="SMSOK";
                            message=message2;    
                         
                        break;
                  
                }
                BAOS=new ByteArrayOutputStream();
                DataOutputStream DOS=new DataOutputStream(BAOS);
                
                try{
                    DOS.writeUTF(state);
                    DOS.writeUTF(message);
                }catch(IOException ex){
                    System.out.println("Exception2 : "+ex.getMessage());
                } 
                
                MSG=BAOS.toByteArray();

                this.client.getOutputStream().write(MSG);
                
                
                
                
                
                
            }
        
        }catch(IOException ex){ 
            System.out.println("Exception3 : "+ex.getMessage());
        }
        
        
        
    }
    
}
