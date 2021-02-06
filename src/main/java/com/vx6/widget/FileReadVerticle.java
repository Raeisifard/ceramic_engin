package com.vx6.widget;

import com.stevesoft.pat.FileRegex;
import com.vx6.master.MasterVerticle;
import com.vx6.utils.Library;
import com.vx6.utils.UnicodeReader;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

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
    if (autoNext && !holdOn){
      eb.publish(addressBook.getTrigger(), "Next message", new DeliveryOptions().addHeader("cmd", "next"));
    }
  }

  @Override
  public <T> void next(Message<T> tMessage) {
    try {
      String msg = getNextLineFromFile();
      if (msg != null) {
        eb.publish(addressBook.getResult(), msg, addressBook.getDeliveryOptions().addHeader("count", count.toString()));
        if (autoNext) {
          eb.publish(addressBook.getTrigger(), "Next message", new DeliveryOptions().addHeader("cmd", "next"));
        }
      }
    } catch (Exception e) {
      eb.publish(addressBook.getError(), "Get next line from file was unsuccessful");
    }
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
        return null;
      }
      new File(this.fileName + this.FileNameLockExt).delete();
      if (!new File(this.fileName).renameTo(new File(this.fileName + this.FileNameLockExt)))
        this.eb.publish(addressBook.getError(), "can not rename file=" + this.fileName + " to=" + this.fileName + this.FileNameLockExt,
          addressBook.getDeliveryOptions());
      try {
        this.in = new BufferedReader(new UnicodeReader(new FileInputStream(this.fileName + this.FileNameLockExt), "cp1256"));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        this.eb.publish(addressBook.getError(), Library.getMessageEx(e), addressBook.getDeliveryOptions());
      }
    }
    String lineStr = in.readLine();
    if (lineStr == null) {//End of file
      this.eb.publish(addressBook.getError(), "End of file!", addressBook.getDeliveryOptions()
        .addHeader("fileName", this.fileName).addHeader("count", this.count + ""));
      in.close();
      if (!new File(this.fileName + this.FileNameLockExt).renameTo(new File(this.fileName + this.FileNameDoneExt)))
        this.eb.publish(addressBook.getError(),
          "can not rename file=" + this.fileName + this.FileNameLockExt + " to=" + this.fileName + this.FileNameDoneExt,
          addressBook.getDeliveryOptions().addHeader("result", "error").addHeader("fileName", this.fileName).addHeader("count", this.count + ""));
      in = null;
      count = 0;
      this.fileName = "";
      return null;
    }
    this.count++;
    return lineStr;
  }
}
