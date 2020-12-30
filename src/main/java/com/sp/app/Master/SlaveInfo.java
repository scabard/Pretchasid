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
    public String[] images;

    public SlaveInfo ( Socket inpsSock, String inpname, DataInputStream inpdis, DataOutputStream inpdos, String inpip, int inpport, String[] inpimages ) {
        sSock = inpsSock;
        name = inpname;
        dis = inpdis;
        dos = inpdos;
        ip = inpip;
        port = inpport;
        images = inpimages;
        isAvailable = true;
    }
}