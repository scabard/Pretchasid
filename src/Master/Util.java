import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class Util {
    public static synchronized int workqSize() {
        return Master.workQ.size();
    }

    public static synchronized boolean workqisEmpty() {
        return Master.workQ.isEmpty();
    }

    public static synchronized String workqRemove() {
        return Master.workQ.remove();
    }

    public static synchronized boolean slavefisEmpty() {
        return Master.slaveF.isEmpty();
    }

    public static synchronized String slavefRemove() {
        return Master.slaveF.remove();
    }

    public static synchronized int slavefSize() {
        return Master.slaveF.size();
    }
    public static synchronized void addWork(String name) {
        Master.workQ.add(name);
        System.out.println("Work added by client " + name);
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
}
