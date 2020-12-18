package com.sp.app.Client;

import java.io.*;

public class TaskInfo {
    public String fileName;
    public FileInputStream fis;
    public long fileL;

    public TaskInfo ( String inpfileName, FileInputStream inpfis, long inpfileL ) {
        fileName = inpfileName;
        fis = inpfis;
        fileL = inpfileL;
    }
}