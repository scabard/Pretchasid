package com.sp.app.Master;

import java.io.*;
import java.net.*;
import java.util.*;

public class RunSlave implements Runnable {
    String serverPort = null;
    String name = null;

    public RunSlave(String inpname, String inpserverPort ) {
        name = inpname;
        serverPort = inpserverPort;
    }

    public void run() {
        String[] command = new String[] {"java", "Slave",serverPort,name};
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process;
        try {
            process = builder.start();

            OutputStream stdin = process.getOutputStream();
            InputStream stdout = process.getInputStream();
            InputStream stderr = process.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
            BufferedReader error = new BufferedReader(new InputStreamReader(stderr));

            String line;
            while ((line = reader.readLine()) != null) writer.write(line.concat("\n"));
            while ((line = error.readLine()) != null) writer.write(line.concat("\n"));
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }


    }
}