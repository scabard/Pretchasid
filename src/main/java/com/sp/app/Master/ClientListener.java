package com.sp.app.Master;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.ServerSocket;

import org.json.JSONObject;
import org.json.XML;

public class ClientListener implements Runnable {
    ServerSocket cSSock;

    public ClientListener( ServerSocket inpcSSock ) {
        cSSock = inpcSSock;
    }

    public void run() {
        Socket cS;
        Thread clientT;
        ClientHandler clientH;
        DataInputStream dis;
        DataOutputStream dos;
        try {
            while(true) {
                cS = cSSock.accept();
                System.out.println("New Client Request Received");
                dis = new DataInputStream(cS.getInputStream());
                dos = new DataOutputStream(cS.getOutputStream());

                clientH = new ClientHandler(cS, dis, dos);
                clientT = new Thread(clientH);

                clientT.start();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    String name;
    Socket cSock;
    DataInputStream dis;
    DataOutputStream dos;
    boolean reg;

    public ClientHandler( Socket inpcSock, DataInputStream inpdis, DataOutputStream inpdos ) {
        cSock = inpcSock;
        dis = inpdis;
        dos = inpdos;
        reg = false;
    }

    public void run() {
        register();
        String msg;
        try {
            while(reg) {
                msg = dis.readUTF();
                JSONObject recvJSON = XML.toJSONObject(msg);
                String msgType = recvJSON.getString("type");
                if (msgType.equals("req")) {
                    String image = recvJSON.getString("image");
                    Util.addWork(new WorkInfo(name, image));
                } else if (msgType.equals("exit")) {
                    reg = false;
                    dis.close();
                    cSock.close();
                    cSock.close();
                    System.out.println("Client " + name + " exiting...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void register() {
        String recv;
        try {
            while (!reg) {
                recv = dis.readUTF();
                JSONObject recvJSON = XML.toJSONObject(recv);
                String msgType = recvJSON.getString("ty");
                if(msgType.equals("r")) {
                    name = recvJSON.getString("name");
                    if ( Util.checkClientNames(name) ) {
                        JSONObject msgJSON = new JSONObject();
                        msgJSON.put("type","regfail");
                        msgJSON.put("msg","Name already taken");
                        String msg = XML.toString(msgJSON);
                        dos.writeUTF(msg);
                    }
                    else {
                        JSONObject msgJSON = new JSONObject();
                        msgJSON.put("type","regsuccess");
                        String msg = XML.toString(msgJSON);
                        dos.writeUTF(msg);

                        ClientInfo cInfo = new ClientInfo( cSock, name, dis, dos );
                        Util.addClient(name, cInfo);
                        reg = true;
                    }
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}