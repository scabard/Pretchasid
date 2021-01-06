package com.sp.app.Master;

import java.util.Arrays;
import java.lang.StringBuilder;
import java.lang.Math;

public class Util {
    public static synchronized int workqSize() {
        return Master.workQ.size();
    }

    public static synchronized boolean workqisEmpty() {
        return Master.workQ.isEmpty();
    }

    public static synchronized WorkInfo workqRemove() {
        return Master.workQ.remove();
    }

    public static synchronized void slavefAdd(String name) {
        Master.slaveF.add(name);
    }

    public static synchronized boolean slavefisEmpty() {
        return Master.slaveF.isEmpty();
    }

    public static synchronized String slavefRemove() {
        return Master.slaveF.remove(0);
    }

    public static synchronized Boolean slavefRemove(String name) {
        return Master.slaveF.remove(name);
    }

    public static synchronized int slavefSize() {
        return Master.slaveF.size();
    }

    public static synchronized void workMapPut(String key, WorkMapInfo work) {
        Master.workMap.put(key,work);
    }

    public static synchronized void addWork(WorkInfo work) {
        Master.workQ.add(work);
        System.out.println("Work added by client " + work.name);
    }

    public static synchronized void addClient( String name, ClientInfo cInfo ) {
        Master.clientInfo.put(name, cInfo);
        System.out.println("Registered Client " + name);
    }

    public static synchronized void addSlave( String name, SlaveInfo sInfo ) {
        Master.slaveInfo.put(name, sInfo);
        Master.slaveF.add(name);
        System.out.println("Registered Slave " + name);
    }

    public static synchronized void setSlaveAcceptKey( String name, boolean status ) {
        if (Master.slaveInfo.containsKey(name)) {
            ((SlaveInfo)Master.slaveInfo.get(name)).acceptKey = status;
        }
    }

    public static synchronized boolean getSlaveAcceptKey( String name ) {
        if (Master.slaveInfo.containsKey(name)) {
            return ((SlaveInfo)Master.slaveInfo.get(name)).acceptKey;
        }
        return false;
    }

    public static Object getClient(String name) {
        if (Master.clientInfo.containsKey(name)) {
            return Master.clientInfo.get(name);
        } else {
            return null;
        }
    }

    public static Object getSlave(String name) {
        if (Master.slaveInfo.containsKey(name)) {
            return Master.slaveInfo.get(name);
        } else {
            return null;
        }
    }

    public static String findImage(String image) {
        for(String name: Master.slaveF) {
            SlaveInfo slave = (SlaveInfo)Master.slaveInfo.get(name);
            if( Arrays.asList(slave.images).contains(image) ) {
                return name;
            }
        }
        return null;
    }

    public static boolean checkSlaveNames( String name ) {
        return Master.slaveInfo.containsKey(name);
    }

    public static void removeSlave(String name) {
        Master.slaveInfo.remove(name);
    }

    public static boolean checkClientNames( String name ) {
        return Master.clientInfo.containsKey(name);
    }

    public static void removeClient(String name) {
        Master.workQ.remove(getClient(name));
        Master.clientInfo.remove(name);
        Master.workMap.remove(name);
    }

    public static boolean checkWorkMap( String key ) {
        return Master.clientInfo.containsKey(key);
    }

    public static String getRandString(int n) 
    {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "abcdefghijklmnopqrstuvxyz"; 
        StringBuilder sb = new StringBuilder(n); 
        for (int i = 0; i < n; i++) { 
            int index = (int)(AlphaNumericString.length() * Math.random()); 
            sb.append(AlphaNumericString.charAt(index)); 
        }
        return sb.toString(); 
    }
}
