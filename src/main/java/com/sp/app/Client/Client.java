package com.sp.app.Client;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.JSONObject;
import org.json.XML;

public class Client {
    static boolean requested;
    static TaskInfo tInfo;

    public static void main(String args[]) {
        int serverPort = Integer.parseInt(args[0]);
        InetAddress ip;
        Socket s;
        DataInputStream ipServer;
        DataOutputStream opServer;
        String msg,res;
        Scanner sc= new Scanner(System.in);

        if (args.length < 1) {
            System.err.println("Usage: java Client <ServerPort>");
            System.exit(0);
        }

        System.out.print("Enter name: ");
        String name = sc.nextLine();
        JSONObject msgJSON = new JSONObject();
        msgJSON.put("ty","r");
        msgJSON.put("name",name);
        msg = XML.toString(msgJSON);

        try {
            ip = InetAddress.getByName("localhost");

            s = new Socket(ip, serverPort);

            ipServer = new DataInputStream(s.getInputStream());
            opServer = new DataOutputStream(s.getOutputStream());

            opServer.writeUTF(msg);

            ListenServer l = new ListenServer(ipServer);
            Thread t = new Thread(l);
            t.start();

            while(true) {
                System.out.println("1. Request Work\n2. Exit\n");
                res = sc.nextLine();

                if (res.equals("1")) {
                    JSONObject obj = new JSONObject();
                    System.out.print("Enter file name: ");
                    String file = sc.nextLine();
                    File fileP = new File(file);
                    if(fileP == null) {
                        continue;
                    }
                    FileInputStream fis = new FileInputStream(fileP);
                    long fileL = fileP.length();

                    obj.put("type","req");
                    msg = XML.toString(obj);
                    opServer.writeUTF(msg);

                    TaskInfo task = new TaskInfo( file, fis, fileL );
                    putTask( task );

                    request(true);
                    while(isRequested()) {

                    }
                } else if (res.equals("2")) {
                    JSONObject obj = new JSONObject();
                    obj.put("type","exit");
                    msg = XML.toString(obj);
                    System.out.println("Exiting...");
                    opServer.writeUTF(msg);
                    s.close();
                    t.interrupt();
                } else {
                    System.out.println("Invalid response.");
                }
            }

        } catch( UnknownHostException e ){
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static synchronized boolean isRequested() {
        return requested;
    }

    public static synchronized void request(boolean req) {
        requested = req;
    }

    public static synchronized TaskInfo getTask() {
        return tInfo;
    }

    public static synchronized void putTask(TaskInfo inptInfo) {
        tInfo = inptInfo;
    }
}

class ListenServer implements Runnable
{
    DataInputStream ipServer;
    String msg;

    public ListenServer(DataInputStream ipS)
    {
        ipServer = ipS;
    }

    public void run()
    {
        try {
            msg = ipServer.readUTF();
            JSONObject recvJSON = XML.toJSONObject(msg);
            String msgType = recvJSON.getString("type");
            if ( msgType.equals("work") ) {
                String name = recvJSON.getString("name");
                String ip = recvJSON.getString("ip");
                int port = recvJSON.getInt("port");
                InetAddress ipS = InetAddress.getByName(ip);
                Socket s = new Socket( ip, port );
                DataInputStream disTask = new DataInputStream(s.getInputStream()); 
                DataOutputStream dosTask = new DataOutputStream(s.getOutputStream()); 
                TaskInfo task = Client.getTask();
                
                JSONObject obj = new JSONObject();
                obj.put("type","work");
                obj.put("file",task.fileName);
                obj.put("length",task.fileL);
                String msgS = XML.toString(obj);
                dosTask.writeUTF(msgS);

                msg = disTask.readUTF();
                if(msg.equals("fail")) {
                    Client.request(false);
                    Thread.currentThread().interrupt();
                }

                BufferedInputStream bis = new BufferedInputStream(task.fis);
                long size = 0;
                while (size < task.fileL) {
                    long fSize = 10000;
                    if(fSize + size <= task.fileL) {
                        size+=fSize;
                    }
                    else {
                        size=task.fileL-fSize;
                        fSize+=size;
                    }
                    byte[] fileD = new byte[(int)fSize];
                    bis.read(fileD,0,(int)fSize);
                    dosTask.write(fileD);
                }
                dosTask.flush();
                System.out.println("File Sent");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}