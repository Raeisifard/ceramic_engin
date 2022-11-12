import com.stevesoft.pat.FileRegex;
import com.vx6.master.MasterVerticle;
import com.vx6.utils.Library;
import com.vx6.utils.PersianCalendarBean;
import com.vx6.utils.UnicodeReader;
import com.vx6.tools.Item;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.io.*;
import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;

public class CheckUniqKey extends MasterVerticle {
    private int keyLen = 12;
    private String keyPath = ".\\_Keys\\";
    private String keyFilePrefix = "Unq";
    private String keyFileExt = ".keys";
    private int deleteOldFilesDay = 2;
    private int maxCacheCount = 3;
    private TreeMap<String, Item> keys;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        JsonObject conf = config().getJsonObject("dataSource").getJsonObject("KEY-File");
        this.keyLen = conf.getInteger("keyLen");
        this.keyPath = conf.getString("keyPath");
        this.keyFilePrefix = conf.getString("keyFilePrefix");
        this.keyFileExt = conf.getString("keyFileExt");
        this.deleteOldFilesDay = conf.getInteger("deleteOldFilesDay");
        this.maxCacheCount = conf.getInteger("maxCacheCount");
        if (!Library.pathExist(this.keyPath))
            initPromise.fail("UniqKey Path Not Found =" + this.keyPath);
        else {
            keys = new TreeMap<String, Item>();
            initPromise.complete();
        }
    }

    @Override
    public <T> void process(Message<T> tMessage) {
        if (this.setting.getBoolean("enable") && tMessage.headers().contains("key")) {
            try {
                if (checkKey(tMessage.headers().get("key"))) {
                    publishOut(1, tMessage.body(), addressBook.getDeliveryOptions(tMessage));//The key was unique.
                } else {
                    publishOut(0, tMessage.body(), addressBook.getDeliveryOptions(tMessage));//The key wasn't unique.
                }
            } catch (Exception e) {
                sendException(e);
            }
        } else {
            publishOut(1, tMessage.body(), addressBook.getDeliveryOptions(tMessage));//The unique is not care.
        }
    }

    private String makeKeyFileName(String akeyId) {
        return keyFilePrefix + akeyId + keyFileExt;
    }

    private void saveKeys(String keyId, TreeSet<String> saveKeys) {
        String keysFileName = keyPath + makeKeyFileName(keyId);

        /*if (UserConfiguration.userConfig.isLogDetail())
            Log.log.addLog("Unique save File = " + keysFileName + " Size = " + saveKeys.size());*/
        BufferedWriter out = null;
        String key;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(keysFileName), "cp1256"));

            while ((key = saveKeys.pollFirst()) != null) {
                out.write(key + System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            //Log.log.addExceptLog(e, true);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                //Log.log.addExceptLog(e, true);
            }
        }
    }

    private TreeSet<String> loadKeys(String keyId) {
        TreeSet<String> newKeys = new TreeSet<String>();
        String keysFileName = keyPath + makeKeyFileName(keyId);
        if (new File(keysFileName).exists()) {
            BufferedReader in = null;
            String key;
            try {
                in = new BufferedReader(new UnicodeReader(new FileInputStream(keysFileName), "cp1256"));
                while ((key = in.readLine()) != null) {
                    if (key.isEmpty())
                        continue;
                    newKeys.add(key);
                }
            } catch (Exception e) {
                // Log.log.addExceptLog(e, true);
            } finally {
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e) {
                    // Log.log.addExceptLog(e, true);
                }
            }
            /*if (UserConfiguration.userConfig.isLogDetail())
                Log.log.addLog("Unique Load File = " + keysFileName + " Size = " + newKeys.size());*/
        }
        return newKeys;
    }

    public void shutdown() {
        // if already contains data then save it
        for (String keyId : keys.keySet()) {
            if (keys.get(keyId).modified)
                saveKeys(keyId, keys.get(keyId).data);
        }
    }

    public boolean checkKey(String aKey) throws Exception {
        try {
            if (aKey.isEmpty())  // used for forced "SendSMS" messages without key
                return true;
            String aKeyId = aKey.substring(0, keyLen);
            if (!keys.containsKey(aKeyId)) {
                // not already have a keyId
                if (keys.size() >= maxCacheCount) {
                    // extract first item
                    String firstKey = keys.firstKey();
                    if (keys.get(firstKey).modified)
                        saveKeys(firstKey, keys.get(firstKey).data);
                    keys.remove(firstKey);
                }
                //create new packet
                keys.put(aKeyId, new Item(loadKeys(aKeyId)));
            }
            return (keys.get(aKeyId).add(aKey));
        } catch (Exception e) {
            throw e;
        }
    }

    private String[] listMaskFile(String path, String masks) {
        TreeSet<String> all = new TreeSet<String>();
        StringBuilder masksStr = new StringBuilder(masks);
        while (masksStr.length() > 0) {
            String mask = Library.getToken(masksStr, ",");

            String[] files = new File(path).list(new FileRegex(mask));
            if (files == null) {
                continue;
            }
            for (String file : files) {
                if (file.contains(".$"))
                    continue;
                all.add(file);
            }
        }

        return all.toArray(new String[all.size()]);
    }

    public void clearFiles() {
        final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
        long dt = new Date().getTime() - (deleteOldFilesDay * MILLIS_IN_A_DAY);
        String ss = PersianCalendarBean.getPersianDateSimple(new Date(dt)).substring(0, keyLen);
        String boundaryFileName = makeKeyFileName(ss);

        String[] files = listMaskFile(keyPath, keyFilePrefix + "*" + keyFileExt);
        for (String file : files) {
            if (file.compareToIgnoreCase(boundaryFileName) < 0) {
                new File(keyPath + file).delete();
//                System.out.println("delete file " + file);
            }
        }
    }
}