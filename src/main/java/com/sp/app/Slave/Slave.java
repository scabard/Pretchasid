package com.sp.app.Slave;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;
import org.json.XML;

public class Slave {
    static String currIP;
    static int currPort;
    static DataInputStream disServer;
    static DataOutputStream dosServer;

    public static void available() {
        try {
            dosServer.writeUTF("available");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int serverPort = Integer.parseInt(args[0]);
        String name = args[1];
        String msg;
        InetAddress ip;
        Socket s;
        DataInputStream ipServer;
        DataOutputStream opServer;
        ServerSocket sock;

        JSONObject msgJSON = new JSONObject();
        msgJSON.put("ty","r");
        msgJSON.put("name",name);

        try{
            DockerUtil docker = new DockerUtil();
            List<String> imgList = docker.imgTagList();
            String images = String.join(" , ", imgList);

            ip = InetAddress.getByName("localhost");

            s = new Socket(ip, serverPort);

            disServer = ipServer = new DataInputStream(s.getInputStream());
            dosServer = opServer = new DataOutputStream(s.getOutputStream());
            currIP = s.getLocalAddress().getHostAddress();
            msgJSON.put("ip",currIP);

            sock = new ServerSocket(0);
            currPort = sock.getLocalPort();
            msgJSON.put("port",currPort);
            
            msgJSON.put("images",images);

            TaskListener tL = new TaskListener(sock);
            Thread th = new Thread(tL);
            th.start();

            msg = XML.toString(msgJSON);
            opServer.writeUTF(msg);

            while(true) {
                msg = ipServer.readUTF();
                Thread task = new Thread(new PerformTask(opServer, msg));
                task.start();
            }

        } catch( UnknownHostException e ){
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}

class PerformTask implements Runnable
{
    DataOutputStream opServer;
    String msg;

    public PerformTask(DataOutputStream opS, String ms)
    {
        opServer = opS;
        msg = ms;
    }

    public void run()
    {
        try {
            Thread.sleep(5000);
            System.out.println(msg);
            opServer.writeUTF("done");
        } catch( InterruptedException e ) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}