package com.vx6.widget;

import com.vx6.master.MasterVerticle;
import com.vx6.utils.Library;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileWriteVerticle extends MasterVerticle {
  private String filePath, FileNameMask, FileNameLockExt;
  private boolean autoName;
  private BufferedWriter flw = null;
  private String tStamp = "";
  private String currentFileName;
  private String newFileName;
  private String currentUsedTime;
  private final String lineSep = System.getProperty("line.separator");
  private long timerID;

  @Override
  public void initialize(Promise<Void> initPromise) {
    this.filePath = Library.correctPathName(setting.getString("filePath"));
    this.FileNameMask = setting.getString("fileNameMask");
    this.FileNameLockExt = Library.correctFileExt(setting.getString("fileNameLockExt"));
    this.autoName = this.FileNameMask.contains("date(");
    if (this.autoName) {
      this.tStamp = this.FileNameMask.substring(this.FileNameMask.indexOf("(") + 1, this.FileNameMask.indexOf(")"));
      if (this.tStamp.trim().isEmpty())
        this.tStamp = "yyyyMMdd_HHmm";
    }
    timerID = vertx.setPeriodic(5000, id -> {
      if (flw != null) {
        try {
          flw.flush();
        } catch (IOException e) {
          //e.printStackTrace();
        }
      }
    });
    if ((addressBook.getTriggerIns().size() == 0 && validateFileOrFolder()) || addressBook.getTriggerIns().size() > 0) {
      initPromise.complete();
    } else
      initPromise.fail("File Writer is not setting well!");
  }

  @Override
  public <T> void process(Message<T> tMessage) {
    //Check we have open file to write
    if (flw != null) {
      //Check we need new file or use the same opened file stream
      if (this.autoName) {
        String newTime = getNewTimeWithFormat(this.tStamp);
        if (!currentUsedTime.equalsIgnoreCase(newTime)) {
          //Time changed so close oldy and open new file with new name
          try {
            flw.flush();
            flw.close();
            this.newFileName = this.FileNameMask.replace("date(" + this.tStamp + ")", newTime);
            if (!new File(this.filePath + this.currentFileName + this.FileNameLockExt)
              .renameTo(new File(this.filePath + this.currentFileName)))
              this.eb.publish(addressBook.getError(),
                "Could not eliminate \"" + this.FileNameLockExt + "\" from file name!",
                addressBook.getDeliveryOptions().addHeader("cause", "rename-failed")
                  .addHeader("sourceName", this.currentFileName + this.FileNameLockExt)
                  .addHeader("DestName", this.currentFileName));
            flw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.filePath + this.newFileName + this.FileNameLockExt, true), "cp1256"));
          } catch (IOException e) {
            this.eb.publish(addressBook.getError(),
              e.getMessage(),
              addressBook.getDeliveryOptions().addHeader("cause", e.getMessage())
                .addHeader("sourceName", this.currentFileName + this.FileNameLockExt)
                .addHeader("DestName", this.currentFileName));
            System.err.println("We faced with error: " + e.getMessage());
            e.printStackTrace();
          } finally {
            this.currentUsedTime = newTime;
            this.currentFileName = this.newFileName;
          }
        }
      }
    } else {
      try {
        if (this.autoName) {
          this.currentUsedTime = this.getNewTimeWithFormat(this.tStamp);
          this.currentFileName = this.FileNameMask.replace("date(" + this.tStamp + ")", this.currentUsedTime);
        } else {
          this.currentFileName = this.FileNameMask;
        }
        flw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.filePath + this.currentFileName + this.FileNameLockExt, true), "cp1256"));
      } catch (UnsupportedEncodingException | FileNotFoundException e) {
        this.eb.publish(addressBook.getError(),
          e.getMessage(),
          addressBook.getDeliveryOptions().addHeader("cause", e.getMessage()));
        e.printStackTrace();
      }
    }
    try {
      flw.write(tMessage.body() + lineSep);
      if (outputConnected)
        flw.flush();
      eb.publish(addressBook.getResult(), process(tMessage.body().toString()), addressBook.getDeliveryOptions(tMessage));
    } catch (IOException e) {
      this.eb.publish(addressBook.getError(),
        tMessage.body(),
        addressBook.getDeliveryOptions(tMessage).addHeader("cause", e.getMessage()));
      e.printStackTrace();
    }
  }

  @Override
  public <T> void flush(Message<T> tMessage) {
    try {
      if (this.flw != null) {
        this.flw.flush();
      }
    } catch (Exception e) {
      if (errorConnected) {
        eb.publish(addressBook.getError(), e.getMessage(), addressBook.getDeliveryOptions().addHeader("error", "FLUSH_FAILED"));
      }
    }
  }

  @Override
  public void stop() {
    vertx.cancelTimer(timerID);
    shutdown();
  }

  private boolean validateFileOrFolder() {
    return this.filePath != null && Files.exists(Paths.get(this.filePath));
  }

  private String getNewTimeWithFormat(String tStamp) {
    return new SimpleDateFormat(tStamp).format(new Date());
  }

  private void shutdown() {
    try {
      if (this.flw != null) {
        flw.flush();
        this.flw.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      this.flw = null;
    }
  }
}
