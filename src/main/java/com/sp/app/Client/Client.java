package com.sp.app.Client;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.JSONObject;
import org.json.XML;
// import org.json.JSONTokener;

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
                    System.out.print("Enter config file name: ");
                    String configFile = sc.nextLine();

                    BufferedReader reader = new BufferedReader(new FileReader(configFile));
                    StringBuilder stringBuilder = new StringBuilder();
                    char[] buffer = new char[10];
                    while (reader.read(buffer) != -1) {
                        stringBuilder.append(new String(buffer));
                        buffer = new char[10];
                    }
                    reader.close();

                    String configContent = stringBuilder.toString();
                    JSONObject config = new JSONObject(configContent);

                    String file = config.getString("file");
                    File fileP = new File(file);
                    if(fileP == null) {
                        continue;
                    }
                    FileInputStream fis = new FileInputStream(fileP);
                    long fileL = fileP.length();

                    obj.put("type","req");
                    obj.put("image",config.getString("image"));
                    msg = XML.toString(obj);
                    opServer.writeUTF(msg);

                    TaskInfo task = new TaskInfo( config, fis, fileL );
                    putTask( task );

                    request(true);
                    try {
                        while(isRequested()) {
                            Thread.sleep(1000);
                        }
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                } else if (res.equals("2")) {
                    JSONObject obj = new JSONObject();
                    obj.put("type","exit");
                    msg = XML.toString(obj);
                    System.out.println("Exiting...");
                    opServer.writeUTF(msg);
                    s.close();
                    t.interrupt();
                    break;
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

        sc.close();
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
                // String name = recvJSON.getString("name");
                String ip = recvJSON.getString("ip");
                int port = recvJSON.getInt("port");
                // InetAddress ipS = InetAddress.getByName(ip);
                Socket s = new Socket( ip, port );
                DataInputStream disTask = new DataInputStream(s.getInputStream());
                DataOutputStream dosTask = new DataOutputStream(s.getOutputStream());
                TaskInfo task = Client.getTask();

                task.config.put("type","work");
                task.config.put("file","test.zip");
                task.config.put("length",task.fileL);
                String msgS = XML.toString(task.config);
                dosTask.writeUTF(msgS);

                msg = disTask.readUTF();
                if(msg.equals("fail")) {
                    Client.request(false);
                    Thread.currentThread().interrupt();
                }

                BufferedInputStream bis = new BufferedInputStream(task.fis);
                long size = 0;
                while (size < task.fileL) {
                    long fSize = 5000;
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

                msg = disTask.readUTF();
                recvJSON = XML.toJSONObject(msg);
                msgType = recvJSON.getString("type");
                if(msgType.equals("output")) {
                    String execOutput = recvJSON.getString("output");
                    System.out.println(execOutput);
                }

                Client.request(false);
                s.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}