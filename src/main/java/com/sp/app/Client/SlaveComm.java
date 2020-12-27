package com.sp.app.Client;

import java.net.*;
import java.io.*;
import java.util.*;

public class SlaveComm implements Runnable {
    TaskInfo task;
    Socket sock;
    
    public SlaveComm( TaskInfo inptask, Socket inpsock ) {
        task = inptask;
        sock = inpsock;
    }

    public void run() {
        try {
            DataInputStream disTask = new DataInputStream(sock.getInputStream()); 
            DataOutputStream dosTask = new DataOutputStream(sock.getOutputStream()); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}