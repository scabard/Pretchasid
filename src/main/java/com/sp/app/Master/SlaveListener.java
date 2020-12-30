package com.sp.app.Master;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;
import org.json.XML;

public class SlaveListener implements Runnable {
    ServerSocket sSSock;

    public SlaveListener( ServerSocket inpsSSock ) {
        sSSock = inpsSSock;
    }

    public void run() {
        Socket sS;
        Thread slaveT;
        SlaveHandler slaveH;
        DataInputStream dis;
        DataOutputStream dos;
        try {
            while(true) {
                sS = sSSock.accept();
                System.out.println("New Slave Request Received");
                dis = new DataInputStream(sS.getInputStream());
                dos = new DataOutputStream(sS.getOutputStream());

                slaveH = new SlaveHandler(sS, dis, dos);
                slaveT = new Thread(slaveH);

                slaveT.start();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}

class SlaveHandler implements Runnable {
    String name;
    Socket sSock;
    DataInputStream dis;
    DataOutputStream dos;
    boolean reg;
    String[] images;

    public SlaveHandler( Socket inpsSock, DataInputStream inpdis, DataOutputStream inpdos ) {
        sSock = inpsSock;
        dis = inpdis;
        dos = inpdos;
        reg = false;
    }

    public void run() {
        register();
        String recv;

        try {
            while(reg) {
                recv = dis.readUTF();

                if (recv.equals("available")) {
                    System.out.println("Slave " + name + " available again!");
                    Util.slavefAdd(name);
                }
            }
        } catch (IOException e) {
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
                    String ip = recvJSON.getString("ip");
                    int port = recvJSON.getInt("port");
                    String imgList = recvJSON.getString("images");
                    images = imgList.split(" , ");
                    SlaveInfo sInfo = new SlaveInfo( sSock, name, dis, dos, ip, port, images );
                    Util.addSlave(name, sInfo);
                    reg = true;
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}