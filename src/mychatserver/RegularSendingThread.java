/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychatserver;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author User
 */
public class RegularSendingThread extends Thread {    
    
    @Override
    public void run(){
        try{
            ByteArrayOutputStream BAOS=new ByteArrayOutputStream();
            DataOutputStream DOS=new DataOutputStream(BAOS);
            String msg;
            while(true){
                System.out.println("REGULAR SENDING RUN");
                msg="";
                BAOS.reset();  
                
                synchronized(ServerThread.messages){
                     for(String message: ServerThread.messages){                         
                        msg+=message+"|";
                    }
                }
                System.out.println("============================");
                
                String[] strArra=msg.split("\\|");
                System.out.println("SIZE strArray : "+strArra.length);
                
               
                
                for(String str:strArra){                    
                    System.out.println(str);
                }
                System.out.println("----------------------------------------");
                try{
                    DOS.writeInt(msg.length());
                    DOS.writeUTF(msg);
                }catch(IOException ex){
                    System.out.println("Exception : "+ex.getMessage());
                }
                byte[] MSG=BAOS.toByteArray();
                System.out.println("length : "+MSG.length);
                
                String str=new String(MSG);
                String[] strArray=str.split("\\|");
                System.out.println("length array : "+strArray.length);
                for(String st:strArray){                    
                    System.out.println(st);
                }
                
                System.out.println("============================");
                synchronized(ServerThread.users){
                    for(User user: ServerThread.users){
                        System.out.println(msg);
                        user.getSocket2().getOutputStream().write(MSG);
                    }
                }              
                
                Thread.sleep(3000);
            }
                
            
        }catch(Exception ex){
            System.out.println("Exception : "+ex.getMessage());
        }
        
    
    }
}   