package com.sp.app.Master;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;
import org.json.XML;

public class Scheduler implements Runnable{

    public void sendMessage(ClientInfo c, SlaveInfo s) {
        try {
            JSONObject msgJSON = new JSONObject();
            msgJSON.put("type","work");
            msgJSON.put("name",s.name);
            msgJSON.put("ip",s.ip);
            msgJSON.put("port",s.port);
            String msg = XML.toString(msgJSON);

            c.dos.writeUTF(msg);
            System.out.println("Slave " + s.name + " details given to client " + c.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                // System.out.println(Master.workQ.size());
                while (!Util.workqisEmpty() && !Util.slavefisEmpty()) {
                    WorkInfo w = Util.workqRemove();
                    String c = w.name;
                    String img = w.image;

                    String s = Util.findImage(img);
                    if(img!=null) {
                        System.out.println("Image found.");
                        Util.slavefRemove(s);
                    }
                    else {
                        System.out.println("Image not found.");
                        s = Util.slavefRemove();
                    }

                    ClientInfo client = (ClientInfo)Util.getClient(c);
                    SlaveInfo slave = (SlaveInfo)Util.getSlave(s);
                    Util.workMapPut(c, s);

                    System.out.println("Client: " + c + "Slave: " + s);

                    sendMessage(client, slave);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
