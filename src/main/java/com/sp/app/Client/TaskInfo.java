package com.sp.app.Client;

import java.io.*;
import org.json.JSONObject;

public class TaskInfo {
    public JSONObject config;
    public FileInputStream fis;
    public long fileL;

    public TaskInfo ( JSONObject inpconfig, FileInputStream inpfis, long inpfileL ) {
        config = inpconfig;
        fis = inpfis;
        fileL = inpfileL;
    }
}