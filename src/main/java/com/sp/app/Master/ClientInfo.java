package com.sp.app.Master;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientInfo {
    public Socket cSock;
    public String name;
    public DataInputStream dis;
    public DataOutputStream dos;

    public ClientInfo ( Socket inpcSock, String inpname, DataInputStream inpdis, DataOutputStream inpdos ) {
        cSock = inpcSock;
        name = inpname;
        dis = inpdis;
        dos = inpdos;
    }
}