import java.io.*;
import java.net.*;
import java.util.*;

public class Master {
    public static int cPort;
    public static int sPort;
    public static HashMap<String, Object> clientInfo;
    public static HashMap<String, Object> slaveInfo;

    public static void main(String[] args) {

        int nSlaves = Integer.parseInt(args[0]);
        InetAddress ip;
        ServerSocket cSock, sSock;
        Thread th;
        clientInfo = new HashMap<String, Object>();
        slaveInfo = new HashMap<String, Object>();
            
        try {
            cSock = new ServerSocket(0);
            cPort = cSock.getLocalPort();
            System.out.println("Listening for Clients on Port " + cPort);

            sSock = new ServerSocket(0);
            sPort = sSock.getLocalPort();
            System.out.println("Listening for Slaves on Port " + sPort);

            ClientListener cL = new ClientListener(cSock);
            th = new Thread(cL);
            th.start();

            SlaveListener sL = new SlaveListener(sSock);
            th = new Thread(sL);
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

    public static synchronized void addClient( String name, ClientInfo cInfo ) {
        clientInfo.put(name, cInfo);
        System.out.println("Registered Client " + name);
    }

    public static synchronized void addSlave( String name, SlaveInfo sInfo ) {
        slaveInfo.put(name, sInfo);
        System.out.println("Registered Slave " + name);
    }
}