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
            JSONObject msgJSON = new JSONObject();
            msgJSON.put("type","available");
            String msg = XML.toString(msgJSON);
            dosServer.writeUTF(msg);
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
        Scanner sc= new Scanner(System.in);
        String res;

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

            msg = XML.toString(msgJSON);
            opServer.writeUTF(msg);

            msg = ipServer.readUTF();
            msgJSON = XML.toJSONObject(msg);
            String msgType = msgJSON.getString("type");
            if(msgType.equals("regfail")) {
                System.out.println(msgJSON.getString("msg"));
                System.exit(0);
            } else if(msgType.equals("regsuccess")) {
                
                System.out.println("Registration Successful");
            }

            TaskListener tL = new TaskListener(sock);
            Thread th = new Thread(tL);
            th.start();
            
            Thread task = new Thread(new ListenServer(opServer, ipServer));
            task.start();

            while(true) {
                System.out.println("1. Exit");
                res = sc.nextLine();
                if (res.equals("1")) {
                    JSONObject obj = new JSONObject();
                    obj.put("type","exit");
                    msg = XML.toString(obj);
                    System.out.println("Exiting...");
                    opServer.writeUTF(msg);
                    th.interrupt();
                    task.interrupt();
                    s.close();
                    sock.close();
                    break;
                } else {
                    System.out.println("Invalid response.");
                }
            }
        
        System.exit(0);
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

class ListenServer implements Runnable
{
    DataOutputStream opServer;
    DataInputStream ipServer;

    public ListenServer(DataOutputStream opS, DataInputStream ipS)
    {
        opServer = opS;
        ipServer = ipS;
    }

    public void run()
    {
        try {
            String msg;
            while(true) {
                msg = ipServer.readUTF();
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}