import java.io.*;
import java.net.*;
import java.util.*;

public class SlaveInfo {
    public Socket sSock;
    public String name;
    public DataInputStream dis;
    public DataOutputStream dos;
    public boolean isAvailable;

    public SlaveInfo ( Socket inpsSock, String inpname, DataInputStream inpdis, DataOutputStream inpdos ) {
        sSock = inpsSock;
        name = inpname;
        dis = inpdis;
        dos = inpdos;
        isAvailable = true;
    }
}