package com.ceramic.tools;

import io.vertx.core.json.JsonObject;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Account {
    public static JsonObject csv2json(String csv) {
        var split = csv.split("\\|");
        if (isBlank(csv) || split.length < 15)
            return null;
        JsonObject accountInfo = new JsonObject();
        accountInfo.put("AccountNo", split[0].trim());
        accountInfo.put("Enable", "1".equals(split[1]));
        accountInfo.put("CustomerId", split[2]);
        accountInfo.put("TransGroupsStr", split[3]);
        accountInfo.put("Phones", split[4]);
        accountInfo.put("AutoRenewRegister", "1".equals(split[5]));
        accountInfo.put("RemainAmount", Long.parseLong(split[6]));
        accountInfo.put("RemainDays", Long.parseLong(split[7]));
        accountInfo.put("DueDate", split[8].equals("null") ? null : split[8]);
        accountInfo.put("LowLimitCredit", Long.parseLong(split[9]));
        accountInfo.put("LowLimitDebit", Long.parseLong(split[10]));
        accountInfo.put("ExpireDate", split[11].equals("null") ? null : split[11]);
        accountInfo.put("NearExpireNotifyDate", split[12].equals("null") ? null : split[12]);
        accountInfo.put("ExpiredNotifyDate", split[13].equals("null") ? null : split[13]);
        accountInfo.put("EditDT", split[14].equals("null") ? null : split[14]);
        accountInfo.put("AutoRenewRegCancelNotifyDate", split[15].equals("null") ? null : split[15]);
        //accountInfo.put("Flag", split[16].equals("null") ? null : Integer.parseInt(split[16]));
        return accountInfo;
    }

    public static String json2csv(JsonObject json) {
        if (json == null || json.isEmpty())
            return null;
        StringBuilder account = new StringBuilder();
        account.append(json.getString("AccountNo")).append("|").append(json.getBoolean("Enable") ? 1 : 0);
        account.append("|").append(json.getString("CustomerId")).append("|").append(json.getString("TransGroupsStr"));
        account.append("|").append(json.getString("Phones")).append("|").append(json.getBoolean("AutoRenewRegister") ? 1 : 0);
        account.append("|").append(json.getLong("RemainAmount")).append("|").append(json.getLong("RemainDays"));
        account.append("|").append(json.getString("DueDate")).append("|").append(json.getLong("LowLimitCredit"));
        account.append("|").append(json.getLong("LowLimitDebit")).append("|").append(json.getString("ExpireDate"));
        account.append("|").append(json.getString("NearExpireNotifyDate")).append("|").append(json.getString("ExpiredNotifyDate"));
        account.append("|").append(json.getString("EditDT")).append("|").append(json.getString("AutoRenewRegCancelNotifyDate"));
        //account.append("|").append(json.getString("Flag"));
        return account.toString();
    }
}
