package com.sp.app.Slave;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;
import org.json.XML;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

public class TaskListener implements Runnable {
    ServerSocket sock;

    public TaskListener( ServerSocket inpsock ) {
        sock = inpsock;
    }

    public void run() {
        Socket s;
        Thread taskT;
        TaskHandler taskH;
        DataInputStream dis;
        DataOutputStream dos;
        try {
            while(true) {
                s = sock.accept();
                System.out.println("New Task Request Received");
                dis = new DataInputStream(s.getInputStream());
                dos = new DataOutputStream(s.getOutputStream());

                taskH = new TaskHandler(s, dis, dos);
                taskT = new Thread(taskH);

                taskT.start();

                try {
                    taskT.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}

class TaskHandler implements Runnable {
    Socket cSock;
    DataInputStream dis;
    DataOutputStream dos;
    String file;
    long fileL;
    String cmd;
    String image;

    public TaskHandler( Socket inpcSock, DataInputStream inpdis, DataOutputStream inpdos ) {
        cSock = inpcSock;
        dis = inpdis;
        dos = inpdos;
    }

    public void run() {
        config();
        recvFile();
        try {
            unzipFile();
            DockerUtil docker = new DockerUtil();
            docker.imgHandler(image);
            docker.containerHandler(image, cmd);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void config() {
        String recv;
        try {
            recv = dis.readUTF();
            JSONObject recvJSON = XML.toJSONObject(recv);
            String msgType = recvJSON.getString("type");
            if(msgType.equals("work")) {
                file = recvJSON.getString("file");
                fileL = recvJSON.getLong("length");
                cmd = recvJSON.getString("cmd");
                image = recvJSON.getString("image");

                System.out.println("Request Received");
                dos.writeUTF("accept");
            }
            else {
                dos.writeUTF("fail");
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    void recvFile() {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] fileData = new byte[20000];
            long size=0;
            int fSize;
            while((fSize=dis.read(fileData))!=-1 && size<fileL ) {
                bos.write(fileData, 0, fSize);
                size+=fSize;
                if(size>=fileL) {
                    break;
                }
            }
            bos.flush();
            System.out.println("File Received "+file);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    void unzipFile() throws Exception {
        byte[] buffer = new byte[4096];

        File zipFile = new File(file);
        ZipFile zf = new ZipFile(zipFile);

        Enumeration<ZipArchiveEntry> entries = zf.getEntries();

        while(entries.hasMoreElements())
        {
            ZipArchiveEntry ze = entries.nextElement();
            String zefilename = ze.getName();
            String dir = "data/";

            File extfile = new File(dir.concat(zefilename));

            if (ze.isDirectory()) {
                extfile.mkdirs();
            } else {
                extfile.getParentFile().mkdirs();
                InputStream zis = zf.getInputStream(ze);
                FileOutputStream fos = new FileOutputStream(extfile);
                try {
                    int numBytes;
                    while ((numBytes = zis.read(buffer, 0, buffer.length)) != -1)
                        fos.write(buffer, 0, numBytes);
                }
                finally {
                    fos.close();
                    zis.close();
                }
            }

        }
        zf.close();
    }
}