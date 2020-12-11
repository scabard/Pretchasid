import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;
import org.json.XML;

public class Slave {
    public static void main(String[] args) {
        int serverPort = Integer.parseInt(args[0]);
        String name = args[1];
        String msg;
        InetAddress ip;
        Socket s;
        DataInputStream ipServer;
        DataOutputStream opServer;

        JSONObject msgJSON = new JSONObject();
        msgJSON.put("ty","r");
        msgJSON.put("name",name);
        msg = XML.toString(msgJSON);

        try{
            ip = InetAddress.getByName("localhost"); 
            
            s = new Socket(ip, serverPort);
            
            ipServer = new DataInputStream(s.getInputStream()); 
            opServer = new DataOutputStream(s.getOutputStream()); 
        
            opServer.writeUTF(msg);

            while(true) {
                msg = ipServer.readUTF();
                Thread task = new Thread(new PerformTask(opServer, msg));
                task.start();
            }

        } catch( UnknownHostException e ){
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) { 
            e.printStackTrace();
            System.exit(0);
        }
    }
}

class PerformTask implements Runnable
{
    DataOutputStream opServer;
    String msg;

    public PerformTask(DataOutputStream opS, String ms) 
    {
        opServer = opS;
        msg = ms;
    }

    public void run() 
    {
        try {
            Thread.sleep(5000);
            System.out.println(msg);
            opServer.writeUTF("done");
        } catch( InterruptedException e ) { 
            e.printStackTrace();
        } catch (IOException e) { 
            e.printStackTrace();
        }
    }
}