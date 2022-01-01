package com.vx6.widget;

import com.ceramic.shared.ShareableHealthCheckHandler;
import com.stevesoft.pat.FileRegex;
import com.vx6.master.MasterVerticle;
import com.vx6.utils.Library;
import com.vx6.utils.UnicodeReader;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileReadVerticle extends MasterVerticle {
    private String filePath, FileNameMask, FileNameDoneExt, FileNameLockExt;
    private String fileName;
    private Integer count = 0;
    private String mode = "SELECT";
    private BufferedReader in = null;
    private Integer rate = null;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        if (addressBook.getTriggerIns().size() > 0)
            autoNext = false;
        this.filePath = Library.correctPathName(setting.getString("filePath"));
        this.FileNameMask = setting.getString("fileNameMask");
        this.FileNameDoneExt = Library.correctFileExt(setting.getString("fileNameDoneExt"));
        this.FileNameLockExt = Library.correctFileExt(setting.getString("fileNameLockExt"));
        this.mode = setting.getString("mode").toUpperCase();
        if ((mode.equalsIgnoreCase("WATCH") && validatefileOrFolder()) || mode.equalsIgnoreCase("SELECT")) {
            initPromise.complete();
        } else {
            initPromise.fail("File or folder does note exist!");
        }
    }

    @Override
    public <T> void noCmd(Message<T> msg, String cmd) {
        JsonObject jo = (JsonObject) msg.body();
        if (jo.containsKey("autoNext"))
            autoNext = jo.getBoolean("autoNext");
        if (jo.containsKey("rate"))
            this.rate = jo.getInteger("rate");
        if (autoNext && !holdOn) {
            eb.publish(addressBook.getTrigger(), "Next message", addressBook.getDeliveryOptions().addHeader("cmd", "next"));
        }
    }

    @Override
    public <T> void next(Message<T> tMessage) {
        try {
            if (autoNext && this.rate != null && this.rate == 0)
                return;
            if (autoNext && this.rate != null && this.rate > 0)
                this.rate--;
            String msg = getNextLineFromFile();
            if (msg != null) {
                DeliveryOptions dOpt = addressBook.getDeliveryOptions();
                dOpt.addHeader("count", count.toString());
                if (this.rate != null) {
                    dOpt.addHeader("rate", this.rate + "");
                }
                eb.publish(addressBook.getResult(), msg, dOpt);
                this.resultOutboundCount++;
                if (autoNext) {
                    eb.publish(addressBook.getTrigger(), "Next message", addressBook.getDeliveryOptions().addHeader("cmd", "next"));
                }
            }
        } catch (Exception e) {
            eb.publish(addressBook.getError(), "Get next line from file was unsuccessful", addressBook.getDeliveryOptions());
            this.errorOutboundCount++;
        }
    }

    @Override
    public void healthCheck() {
        this.ports
                .put("trigger", this.triggerInboundCount)
                //.put("input", this.inputInboundCount)
                .put("error", this.errorOutboundCount)
                .put("result", this.resultOutboundCount);
        this.health.put("type", addressBook.getType());
        this.health.put("ports", this.ports);
        this.healthCheckHandler = ShareableHealthCheckHandler.create(vertx);
        this.healthCheckHandler.register(
                "status/" + config().getString("graph_id") + "/" + config().getString("id"),
                1000,
                promise -> {
                    promise.complete(Status.OK(getHealth()));
                });
    }

    @Override
    protected JsonObject getHealth() {
        if (!this.inputConnected && buffer == null) {
            this.health.put("holdOn", (this.holdOn ? "1" : "0"));
        }
        return this.health
                .put("ports", this.ports
                        .put("trigger", this.triggerInboundCount)
                        //.put("input", this.inputInboundCount)
                        .put("error", this.errorOutboundCount)
                        .put("result", this.resultOutboundCount));
    }

    private boolean validatefileOrFolder() {
        return this.filePath != null && Files.exists(Paths.get(this.filePath));
    }

    private String getOneFileName() {
        if (Library.isDir(Paths.get(this.filePath))) {
            String[] files = new File(this.filePath).list(new FileRegex(this.FileNameMask));
            Arrays.sort(files);
            for (String file : files) {
                // invalidate on process files
                if (file.endsWith(this.FileNameLockExt))
                    continue;
                if (file.endsWith(this.FileNameDoneExt))
                    continue;
                return (filePath + file);
            }
        } else {//Its file.
            return this.filePath;
        }
        return "";
    }

    private String getNextLineFromFile() throws IOException {
        if (in == null) {
            this.fileName = getOneFileName();
            if (this.fileName.isEmpty()) {//No more file is there
                this.eb.publish(addressBook.getError(), "No more file",
                        addressBook.getDeliveryOptions().addHeader("cause", "no-more-file"));
                this.errorOutboundCount++;
                return null;
            }
            new File(this.fileName + this.FileNameLockExt).delete();
            if (!new File(this.fileName).renameTo(new File(this.fileName + this.FileNameLockExt))) {
                this.eb.publish(addressBook.getError(), "can not rename file=" + this.fileName + " to=" + this.fileName + this.FileNameLockExt,
                        addressBook.getDeliveryOptions());
                this.errorOutboundCount++;
            }
            try {
                this.in = new BufferedReader(new UnicodeReader(new FileInputStream(this.fileName + this.FileNameLockExt), "cp1256"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                this.eb.publish(addressBook.getError(), Library.getMessageEx(e), addressBook.getDeliveryOptions());
                this.errorOutboundCount++;
            }
        }
        String lineStr = in.readLine();
        if (lineStr == null) {//End of file
            this.eb.publish(addressBook.getError(), "End of file!", addressBook.getDeliveryOptions()
                    .addHeader("fileName", this.fileName).addHeader("count", this.count + ""));
            this.errorOutboundCount++;
            in.close();
            if (!new File(this.fileName + this.FileNameLockExt).renameTo(new File(this.fileName + this.FileNameDoneExt))) {
                this.eb.publish(addressBook.getError(),
                        "can not rename file=" + this.fileName + this.FileNameLockExt + " to=" + this.fileName + this.FileNameDoneExt,
                        addressBook.getDeliveryOptions().addHeader("result", "error").addHeader("fileName", this.fileName).addHeader("count", this.count + ""));
                this.errorOutboundCount++;
            }
            in = null;
            count = 0;
            this.fileName = "";
            return null;
        }
        this.count++;
        return lineStr;
    }

    @Override
    public void stop() throws Exception {
        if (in != null) {//End the file
            this.eb.publish(addressBook.getError(), "Closing file...", addressBook.getDeliveryOptions()
                    .addHeader("fileName", this.fileName).addHeader("count", this.count + ""));
            this.errorOutboundCount++;
            in.close();
            in = null;
            this.fileName = "";
        }
    }
}
