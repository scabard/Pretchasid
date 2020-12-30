package com.sp.app.Master;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class Master {
    public static int cPort = 42068;
    public static int sPort = 42069;
    public static HashMap<String, Object> clientInfo;
    public static HashMap<String, Object> slaveInfo;
    public static Queue<WorkInfo> workQ;
    public static LinkedList<String> slaveF;
    public static HashMap<String, String> workMap;

    public static void main(String[] args) {

        int nSlaves = Integer.parseInt(args[0]);
        InetAddress ip;
        ServerSocket cSock, sSock;
        Thread th;
        clientInfo = new HashMap<String, Object>();
        slaveInfo = new HashMap<String, Object>();
        workQ = new LinkedList<WorkInfo>();
        slaveF = new LinkedList<String>();
        workMap = new HashMap<String, String>();
        try {
            cSock = new ServerSocket(cPort);
            // cPort = cSock.getLocalPort();
            System.out.println("Listening for Clients on Port " + cPort);

            sSock = new ServerSocket(sPort);
            // sPort = sSock.getLocalPort();
            System.out.println("Listening for Slaves on Port " + sPort);

            ClientListener cL = new ClientListener(cSock);
            th = new Thread(cL);
            th.start();

            SlaveListener sL = new SlaveListener(sSock);
            th = new Thread(sL);
            th.start();

            Scheduler sc = new Scheduler();
            th = new Thread(sc);
            th.start();

            // RunSlave slave;
            // String sStr = "slave";
            // int port = 2000;

            // for (int i=1; i<=nSlaves; ++i) {
            //     slave = new RunSlave ( sStr.concat(Integer.toString(i)), Integer(sPort).toString() );
            //     s = new Thread (slave);
            //     s.start();
            // }

            while (true) {

            }

            // cSock.close();
            // sSock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public static synchronized void addWork(String name) {
    //     workQ.add(name);
    //     System.out.println("Work added by client " + name);
    // }

    // public static synchronized void addClient( String name, ClientInfo cInfo ) {
    //     clientInfo.put(name, cInfo);
    //     System.out.println("Registered Client " + name);
    // }

    // public static synchronized void addSlave( String name, SlaveInfo sInfo ) {
    //     slaveInfo.put(name, sInfo);
    //     slaveF.add(name);
    //     System.out.println("Registered Slave " + name);
    // }

    // public static Object getClient(String name) {
    //     if (clientInfo.containsKey(name)) {
    //         return clientInfo.get(name);
    //     } else {
    //         return null;
    //     }
    // }

    // public static Object getSlave(String name) {
    //     if (slaveInfo.containsKey(name)) {
    //         return slaveInfo.get(name);
    //     } else {
    //         return null;
    //     }
    // }
}