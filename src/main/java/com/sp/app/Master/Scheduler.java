package com.sp.app.Master;

import org.json.JSONObject;
import org.json.XML;

public class Scheduler implements Runnable{

    public void sendMessage(ClientInfo c, SlaveInfo s, String key) {
        try {
            JSONObject msgJSON = new JSONObject();
            msgJSON.put("type","work");
            msgJSON.put("name",s.name);
            msgJSON.put("ip",s.ip);
            msgJSON.put("port",s.port);
            msgJSON.put("key",key);
            String msg = XML.toString(msgJSON);

            c.dos.writeUTF(msg);
            System.out.println("Slave " + s.name + " details given to client " + c.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsgToSlave(ClientInfo c, SlaveInfo s, String key) {
        try {
            JSONObject msgJSON = new JSONObject();
            msgJSON.put("type","work");
            msgJSON.put("name",c.name);
            msgJSON.put("key",key);
            msgJSON.put("time",1000);
            String msg = XML.toString(msgJSON);

            s.dos.writeUTF(msg);
            System.out.println("Work details given to slave " + s.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                while (!Util.workqisEmpty() && !Util.slavefisEmpty()) {
                    WorkInfo w = Util.workqRemove();
                    String c = w.name;
                    String img = w.image;

                    String s = Util.findImage(img);
                    if(s!=null) {
                        System.out.println("Image found.");
                        Util.slavefRemove(s);
                    }
                    else {
                        System.out.println("Image not found.");
                        s = Util.slavefRemove();
                    }


                    String key;
                    do{
                        key = Util.getRandString(20);
                    } while( Util.checkWorkMap(key) );

                    ClientInfo client = (ClientInfo)Util.getClient(c);
                    SlaveInfo slave = (SlaveInfo)Util.getSlave(s);

                    Util.workMapPut(key, new WorkMapInfo(c,s));

                    System.out.println("Client: " + c + " Slave: " + s);

                    sendMsgToSlave(client,slave,key);
                    final String sName = s;
                    final String cName = c;
                    final ClientInfo cInfo = client;
                    final SlaveInfo sInfo = slave;
                    final String k = key;

                    Runnable runnable = () -> {
                        try{
                            for (int i=0;i<10;++i) {
                                System.out.println("sName");
                                if(Util.getSlaveAcceptKey(sName)==true){
                                    break;
                                }
                                Thread.sleep(100);
                            }
                            sendMessage(cInfo, sInfo, k);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    };
                    Thread thread = new Thread( runnable );
                    thread.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
