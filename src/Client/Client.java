import java.net.*;
import java.io.*;
import java.util.*;
import org.json.JSONObject;
import org.json.XML;

public class Client {
    public static void main(String args[]) {
        int serverPort = Integer.parseInt(args[0]);
        InetAddress ip;
        Socket s;
        DataInputStream ipServer;
        DataOutputStream opServer;
        String msg;
        Scanner sc= new Scanner(System.in);

        if (args.length < 1) {
            System.err.println("Usage: java Client <ServerPort>");
            System.exit(0);
        }

        System.out.print("Enter name: ");
        String name = sc.nextLine();
        JSONObject msgJSON = new JSONObject();
        msgJSON.put("ty","r");
        msgJSON.put("name",name);
        msg = XML.toString(msgJSON);

        try {
            ip = InetAddress.getByName("localhost");

            s = new Socket(ip, serverPort);

            ipServer = new DataInputStream(s.getInputStream());
            opServer = new DataOutputStream(s.getOutputStream());

            opServer.writeUTF(msg);

            ListenTask l = new ListenTask(ipServer);
            Thread t = new Thread(l);
            t.start();

            while(true) {

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

class ListenTask implements Runnable
{
    DataInputStream ipServer;
    String msg;

    public ListenTask(DataInputStream ipS)
    {
        ipServer = ipS;
    }

    public void run()
    {
        try {
            // Thread.sleep(5000);
            // System.out.println(msg);
            msg = ipServer.readUTF();
            System.out.println(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}