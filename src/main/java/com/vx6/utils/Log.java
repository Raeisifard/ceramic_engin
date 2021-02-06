package com.vx6.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Log {
    public static volatile Log log = null;
    private String logDir;
    private String baseFileName;
    private String[] logs;
    private ArrayList<String> fileLogs;
    private LOGMETHOD logMethod;

    public Log(String logDir, String baseFileName, LOGMETHOD logMethod, boolean isUserLog) {
        this.logDir = logDir;
        this.baseFileName = baseFileName;
        logs = new String[256];
        fileLogs = new ArrayList<String>();
        this.logMethod = logMethod;
        if (!isUserLog)
            log = this;
    }

    synchronized public void addLog(String msg) {
        // shiftdown
        for (int i = logs.length - 1; i > 0; i--)
            if (logs[i - 1] != null)
                logs[i] = logs[i - 1];
        logs[0] = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())) + " " + msg;
        fileLogs.add(logs[0]);
        if (fileLogs.size() >= 1024)

            saveLogs();

//MTZ prevent time consuming refresh
//        baseModel.refresh();
    }

    synchronized public void addExceptLog(Throwable ex, boolean addStackTrace) {
        String msg = Library.getMessageEx(ex);
        if (addStackTrace) {
            addLog("!!!!! Exception = " + msg);
            StackTraceElement[] trace = ex.getStackTrace();
            for (int i = 0; i < trace.length; i++)
                addLog("@ " + trace[i]);
        } else {
            addLog("!!!!! Except @" + ex.getStackTrace()[0].toString() + "=" + msg);
        }

    }

    synchronized public void addLogAndSave(String msg) {
        addLog(msg);
        saveLogs();
    }

    synchronized public void saveLogs() {
        if (fileLogs.size() > 0) {
            try {
                if (!logDir.endsWith("\\"))
                    logDir = logDir + "\\";
                new File(logDir).mkdir();

                String outFileName;
                switch (logMethod) {
                    case LOGDAILY:
                        outFileName = logDir + baseFileName + (new SimpleDateFormat("-yyyyMMdd").format(new Date())) + ".Log";
                        break;
                    case LOGMONTHLY:
                        outFileName = logDir + baseFileName + (new SimpleDateFormat("-yyyyMM").format(new Date())) + ".Log";
                        break;
                    case LOGMONTHDIRDAILY:
                        String subDir = logDir + (new SimpleDateFormat("yyyyMM").format(new Date()));
                        new File(subDir).mkdir();
                        outFileName = subDir + "\\" + baseFileName + (new SimpleDateFormat("-yyyyMMdd").format(new Date())) + ".Log";
                        break;
                    default:
                        outFileName = baseFileName + ".Log";
                }

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFileName, true), "cp1256"));
                for (String fileLog : fileLogs) {
                    out.write(fileLog + System.getProperty("line.separator"));
                }
                out.close();
                fileLogs.clear();
            } catch (Exception e) {
                Log.log.addExceptLog(e, true);
            }
        }
    }

    public String[] getLogs() {
        return logs;
    }

    public void setLogs(String[] logs) {
        this.logs = logs;
    }

    public enum LOGMETHOD {LOGDAILY, LOGMONTHLY, LOGMONTHDIRDAILY}
}

