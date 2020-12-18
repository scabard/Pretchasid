package com.sp.app.Slave;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;
import org.json.XML;

public class TaskListener implements Runnable {
    ServerSocket sock;

    public TaskListener( ServerSocket inpsock ) {
        sock = inpsock;
    }

    public void run() {
        Socket s;
        Thread taskT;
        TaskHandler taskH;
        DataInputStream dis;
        DataOutputStream dos;
        try {
            while(true) {
                s = sock.accept();
                System.out.println("New Task Request Received");
                dis = new DataInputStream(s.getInputStream());
                dos = new DataOutputStream(s.getOutputStream());

                taskH = new TaskHandler(s, dis, dos);
                taskT = new Thread(taskH);

                taskT.start();

                try {
                    taskT.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}

class TaskHandler implements Runnable {
    Socket cSock;
    DataInputStream dis;
    DataOutputStream dos;
    String file;
    long fileL;

    public TaskHandler( Socket inpcSock, DataInputStream inpdis, DataOutputStream inpdos ) {
        cSock = inpcSock;
        dis = inpdis;
        dos = inpdos;
    }

    public void run() {
        config();
        recvFile();

    }

    void config() {
        String recv;
        try {
            recv = dis.readUTF();
            JSONObject recvJSON = XML.toJSONObject(recv);
            String msgType = recvJSON.getString("type");
            if(msgType.equals("work")) {
                file = recvJSON.getString("file");
                fileL = recvJSON.getLong("length");

                dos.writeUTF("accept");
            }
            else {
                dos.writeUTF("fail");
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    void recvFile() {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] fileData = new byte[20000];    
            long size=0;
            int fSize;
            while((fSize=dis.read(fileData))!=-1 && size<fileL ) {
                bos.write(fileData, 0, fSize);  
                size+=fSize;
            }
            bos.flush(); 
            System.out.println("File Received "+file);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}