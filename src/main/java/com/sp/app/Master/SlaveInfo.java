package com.sp.app.Master;

import java.io.*;
import java.net.*;
import java.util.*;

public class SlaveInfo {
    public Socket sSock;
    public String name;
    public DataInputStream dis;
    public DataOutputStream dos;
    public boolean isAvailable;
    public String ip;
    public int port;

    public SlaveInfo ( Socket inpsSock, String inpname, DataInputStream inpdis, DataOutputStream inpdos, String inpip, int inpport ) {
        sSock = inpsSock;
        name = inpname;
        dis = inpdis;
        dos = inpdos;
        ip = inpip;
        port = inpport;
        isAvailable = true;
    }
}