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
            msgJSON.put("name",c.name);
            msgJSON.put("dos",c.dos);
            msgJSON.put("dis",c.dis);
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
                    String c = Master.workQ.remove();
                    String s = Master.slaveF.remove();

                    ClientInfo client = (ClientInfo)Util.getClient(c);
                    SlaveInfo slave = (SlaveInfo)Util.getSlave(s);
                    Master.workMap.put(c, s);

                    System.out.println("Client: " + c + "Slave: " + s);

                    sendMessage(client, slave);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
