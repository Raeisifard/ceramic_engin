package com.vx6.utils;

import com.stevesoft.pat.FileRegex;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by M_Tarvirdi on 2/2/2016.
 */

public final class Library {


    private static long currentId = System.currentTimeMillis();

    public static String getToken(StringBuilder aStr, String separator) {
        if (aStr.length() == 0) {
            return aStr.toString();
        } else {
            int pos = aStr.indexOf(separator);
            String found;
            if (pos == -1) {
                found = aStr.toString();
                aStr.setLength(0);
            } else {
                found = aStr.substring(0, pos);
                aStr.delete(0, pos + separator.length());
            }
            return found;
        }
    }

    private static int conv_Byte_To_Int(byte byIn) {
        int intIn = byIn;
        if (intIn < 0)
            intIn = 256 + intIn;
        return intIn;
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hex2Byte(String sHex) {

        byte[] ba = new byte[sHex.length() / 2];
        for (int i = 0; i < sHex.length() / 2; i++) {
            ba[i] = (Integer.decode(
                    "0x" + sHex.substring(i * 2, (i + 1) * 2))).byteValue();
        }
        return ba;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    public static String addLeadingChar(String inStr, String leadStr, int maxLen) {
        if (inStr.length() >= maxLen)
            return inStr;
        else {
            for (int len = inStr.length(); len < maxLen; len++)
                inStr = leadStr + inStr;
            return inStr;
        }
    }

    public static String rightStr(String inStr, int count) {
        if (inStr.length() <= count) {
            return inStr;
        }
        inStr = inStr.substring(inStr.length() - count, inStr.length());
        return inStr;
    }

    public static boolean isDigit(String inStr) {
        if (inStr == null || inStr.isEmpty())
            return false;
        StringBuilder sb = new StringBuilder(inStr);
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) < '0' || sb.charAt(i) > '9')
                return false;
        }
        return true;
    }

    public static boolean isNumber(String inStr) {
        if (inStr == null)
            return false;
        inStr = inStr.trim();
        if (inStr.isEmpty())
            return false;
        try {
            if (inStr.contains(".")) {
                Double.parseDouble(inStr);
                return true;
            } else {
                Long.parseLong(inStr);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static String getPureNumStr(String inStr) {
        if (inStr == null)
            return inStr;
        inStr = inStr.trim();
        if (inStr.isEmpty())
            return inStr;
        try {
            if (inStr.indexOf(".") == -1) {
                return String.format("%d", Long.parseLong(inStr));
            } else {
                while (inStr.endsWith("0"))
                    inStr = inStr.substring(0, inStr.length() - 1);
                int k = inStr.length() - inStr.indexOf(".") - 1;
                return String.format("%." + k + "f", Float.parseFloat(inStr));
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatNumStrComma(String inStr) {
        if (inStr == null)
            return inStr;
        inStr = inStr.trim();
        if (inStr.isEmpty())
            return inStr;
        inStr = inStr.replaceAll(",", "");
        try {
            if (inStr.indexOf(".") == -1) {
                return String.format("%,d", Long.parseLong(inStr));
            } else {
                while (inStr.endsWith("0"))
                    inStr = inStr.substring(0, inStr.length() - 1);
                int k = inStr.length() - inStr.indexOf(".") - 1;
                return String.format("%,." + k + "f", Float.parseFloat(inStr));
            }
        } catch (Exception e) {
            return "";
        }
    }

//    public static String formatNumStrComma(String inStr) {
//        if ((inStr == null) || (inStr.isEmpty()))
//            return inStr;
//        inStr = inStr.replaceAll(",","");
//        String sign = inStr.substring(0, 1);
//        if (!sign.equals("+") && !sign.equals("-"))
//            sign = "";
//        else {
//            inStr = inStr.substring(1);
//            if (sign.equals("+"))
//                sign = "";
//        }
//        StringBuilder sb = new StringBuilder(inStr);
//        if (inStr.contains(".")) {
//            for (int i = inStr.indexOf('.') - 3; i > 0; i = i - 3)
//                sb.insert(i, ',');
//        } else
//            for (int i = inStr.length() - 3; i > 0; i = i - 3)
//                sb.insert(i, ',');
//        return sign + sb.toString();
//    }

    public static String revSign(String inStr) {
        String str = "";
        if (inStr.startsWith("+") || inStr.startsWith("-"))
            str = inStr.substring(1, inStr.length()) + inStr.substring(0, 1);
        else
            str = inStr + "+";
        return str;
    }
    public static String unRevSign(String inStr) {
        String str = "";
        if (inStr.endsWith("+") || inStr.endsWith("-"))
            str = inStr.substring(inStr.length()-1, inStr.length())+inStr.substring(0, inStr.length()-1) ;
        else
            str = "+" +inStr;
        return str;
    }

    public static String noSignNum(String inStr) {
        if (inStr.startsWith("+") || inStr.startsWith("-"))
            inStr = inStr.substring(1);
        return inStr;
    }

    public static boolean pathExist(String pathName) {
        File f = new File(pathName);
        return (f.exists() && f.isDirectory());
    }

    public static boolean fileExist(String fileName) {
        File f = new File(fileName);
        return (f.exists() && f.isFile() && f.canRead());
    }

    public static String correctPathName(String pathName) {
        if (!pathName.isEmpty() && !pathName.endsWith("\\"))
            pathName = pathName + "\\";
        return pathName.replace("\\", System.getProperty("file.separator"));
    }

    public static String correctFileExt(String ext) {
        if (!ext.isEmpty() && !ext.startsWith("."))
            ext = "." + ext;
        return ext;
    }

    public static String getMessageEx(Throwable ex) {
        if (ex.getMessage() != null && !ex.getMessage().isEmpty())
            return ex.getMessage();
        else
            return ex.toString();
    }

    public static String purePDateStr(String inDate) {
        // input : yymmdd yyyymmdd yy/mm/dd yyyy/mm/dd   returns  yyyymmdd
        if (inDate == null)
            return null;
        inDate = inDate.trim().replace("/", "");
        if (inDate.isEmpty() || (inDate.length() != 8 && (inDate.length() != 6)))
            return "";
        if (inDate.length() == 6)  //yyyymmdd
            if (Integer.parseInt(inDate.substring(0, 2)) >= 90)
                inDate = "13" + inDate;
            else
                inDate = "14" + inDate;
        return inDate;
    }

    public static String formatPDateStrLong(String inDate) {
        inDate = purePDateStr(inDate);
        if (inDate == null || inDate.isEmpty())
            return inDate;
        inDate = inDate.replaceAll("/", "");
        return new StringBuffer(inDate).insert(6, "/").insert(4, "/").toString();
    }

//   because of 14K
//    public static String formatPDateStrShort(String inDate) {
//        return formatPDateStrLong(inDate).substring(2);
//    }

    public static String formatPDateStrMonthDay(String inDate) {
        inDate = purePDateStr(inDate);
        if (inDate == null || inDate.isEmpty())
            return inDate;

        return inDate.substring(4);
    }

    public static String pureTimeStr(String inTime) {
        // input hh:mm:ss hhmmss  returns hhmmss
        if ((inTime == null))
            return null;
        inTime = inTime.trim().replace(":", "");
        if (inTime.isEmpty() || inTime.length() != 6)
            return "";
        return inTime;
    }

    public static String formatTimeStr(String inTime) {
        inTime = pureTimeStr(inTime);
        if ((inTime == null) || (inTime.isEmpty()))
            return inTime;
        return new StringBuffer(inTime).insert(4, ":").insert(2, ":").toString();
    }

    static Pattern pattLen10 = Pattern.compile("(^9[^8765]\\d{8})");
    static Pattern pattLen11 = Pattern.compile("(^09[^8765]\\d{8})");
    static Pattern pattLen12 = Pattern.compile("(^989[^8765]\\d{8})");
    static Pattern pattLen13 = Pattern.compile("(^\\+989[^8765]\\d{8})");
    static Pattern pattLen14 = Pattern.compile("(^00989[^8765]\\d{8})");

    //  static Pattern pattNoLen = Pattern.compile("((^00[1-9].[0-9]*)|(^\\+[1-9].[0-9]*))");
    public static String correctPhoneNo(String inStr) {
        if (inStr == null || inStr.trim().isEmpty()) {
            return "";
        }
        inStr = inStr.trim();
        String numPattern = "";
//        if (UserConfiguration.userConfig.engineVer == 3)
//            numPattern = "+98";

        //Check for validation Iran phone number format 9127442266
        //Pattern pattLen10 = Pattern.compile("(^9[^8765]\\d{8})");
        Matcher matcherPattLen10 = pattLen10.matcher(inStr);
        if (matcherPattLen10.matches()) {
            return numPattern + inStr;
        }
        //Check for validation Iran phone number format 09127442266
        //Pattern pattLen11 = Pattern.compile("(^09[^8765]\\d{8})");
        Matcher matcherPattLen11 = pattLen11.matcher(inStr);
        if (matcherPattLen11.matches()) {
            return numPattern + inStr.substring(1, 11);
        }
        //Check for validation Iran phone number format 989127442266
        //Pattern pattLen12 = Pattern.compile("(^989[^8765]\\d{8})");
        Matcher matcherPattLen12 = pattLen12.matcher(inStr);
        if (matcherPattLen12.matches()) {
            return numPattern + inStr.substring(2, 12);
        }
        //Check for validation Iran phone number format +989127442266
        //Pattern pattLen13 = Pattern.compile("(^\\+989[^8765]\\d{8})");
        Matcher matcherPattLen13 = pattLen13.matcher(inStr);
        if (matcherPattLen13.matches()) {
            return numPattern + inStr.substring(3, 13);
        }
        //Check for 14 length
        //Pattern pattLen14 = Pattern.compile("(^00989[^8765]\\d{8})");
        Matcher matcherPattLen14 = pattLen14.matcher(inStr);
        if (matcherPattLen14.matches()) {
            return numPattern + inStr.substring(4, 14);
        }
        //Check for foreign country
//        Pattern pattNoLen = Pattern.compile("((^00[1-9].[0-9]*)|(^\\+[1-9].[0-9]*))");
//        Matcher matcherPattNoLen = pattNoLen.matcher(inStr);
//        if (matcherPattNoLen.matches() && !inStr.startsWith("+989") && !inStr.startsWith("00989")) {
//            if (inStr.startsWith("00")) {
//                return "+" + inStr.substring(2);
//            }
//            return inStr;
//        }
        return "";
    }

    public static String correctPhoneNos(String inStr) {
        ArrayList<String> phones = new ArrayList<String>();
        phones.addAll(Arrays.asList(inStr.replace(",", ";").split(";")));
        for (int idx = phones.size() - 1; idx >= 0; idx--) {
            String phone = phones.get(idx);
            phone = correctPhoneNo(phone);
            if (phone.isEmpty())
                phones.remove(idx);
            else
                phones.set(idx, phone);
        }
        return Library.myJoin(phones, ";");
    }

//    public static String correctPDateYYYYMMDD(String inStr) {
//        if (inStr == null || inStr.trim().isEmpty())
//            return inStr;
//        inStr = inStr.trim().replace("/", "");
//        if (inStr.length() == 6 && Integer.parseInt(inStr.substring(0, 2)) >= 90)
//            return "13" + inStr;
//        else
//            return "14" + inStr;
//    }
//
//    public static String correctTimeHHMMDD(String inStr) {
//        if (inStr == null || inStr.trim().isEmpty())
//            return inStr;
//        return inStr.trim().replace(":", "");
//    }

    public static boolean isRoamingPhoneNo(String inStr) {
//        if (UserConfiguration.userConfig.engineVer == 3)
//            return !inStr.startsWith("+989");
//        else
        return false;

    }

    public static List<String> commonItemsStringArrayList(List<String> one, List<String> two) {
        //return common items
        ArrayList<String> result = new ArrayList<String>();
        if (one == null || two == null)
            return result;
        for (String string : one) {
            if (two.contains(string))
                if (!result.contains(string))
                    result.add(string);
        }
        for (String string : two) {
            if (one.contains(string))
                if (!result.contains(string))
                    result.add(string);
        }
        return result;
    }

    public static List<String> moreItemsStringArrayList(List<String> one, List<String> two) {
        // return more items that present in one but not in Two
        ArrayList<String> result = new ArrayList<String>();
        if (one == null)
            return result;
        if (two == null) {
            result.addAll(one);
            return result;
        }
        for (String string : one) {
            if (!two.contains(string))
                if (!result.contains(string))
                    result.add(string);
        }
        return result;
    }

    public static String myJoin(List<String> list, String seperator) {
        String result = "";
        for (String str : list) {
            if (result.isEmpty())
                result = str;
            else
                result = result + seperator + str;
        }
        return result;
    }

    public static int executeCommand(String command) {

        class StreamGobbler extends Thread {
            InputStream is;
            String type;

            StreamGobbler(InputStream is, String type) {
                this.is = is;
                this.type = type;
            }

            public void run() {
                try {
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        //System.out.println(type + ">" + line);
                    }
                } catch (IOException ioe) {
                    //ioe.printStackTrace();
                }
            }
        }

        try {

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("cmd.exe /C " + command);

            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
            errorGobbler.start();
            outputGobbler.start();

            return proc.waitFor();

        } catch (Throwable t) {
            return -1;
        }

    }

    public static String correctFarsiChars(String inStr) {
        inStr = inStr.replace("ی", "ي");
        return inStr;
    }

    public static synchronized long getUniqueId() {
        return currentId++;
    }

    public static String replaceChar(String str, int fromOffset, int toOffset, char sourceChar, char replaceChar) {
        if (str == null)
            return str;
        char[] chars = str.toCharArray();
        for (int i = fromOffset; i < toOffset; i++) {
            if (chars[i] == sourceChar)
                chars[i] = replaceChar;
        }
        return String.valueOf(chars);
    }


    public static Date nextDay(int dayCount) {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, dayCount);

        return calendar.getTime();
    }

    public static String unicodeNumber(String inStr) {
        String s = "";

        if (inStr.startsWith("غ")) {
            while (!inStr.isEmpty()) {
                String x = inStr.substring(1, 2);
                if (x.equals("°")) s = s + '0';
                else if (x.equals("±")) s = s + '1';
                else if (x.equals("²")) s = s + '2';
                else if (x.equals("³")) s = s + '3';
                else if (x.equals("´")) s = s + '4';
                else if (x.equals("µ")) s = s + '5';
                else if (x.equals("¶")) s = s + '6';
                else if (x.equals("·")) s = s + '7';
                else if (x.equals("¸")) s = s + '8';
                else if (x.equals("¹")) s = s + '9';
                else s = s = " ";

                inStr = inStr.substring(2);
            }
        }
        return s;
    }

    public static boolean betweenHours(int startHour, int endHour, int hour) {
        return (startHour < endHour && (hour >= startHour && hour < endHour)) ||
                (startHour > endHour && (hour < endHour || hour >= startHour));

    }


    public static ArrayList<String> getFilesInDir(String filePaths, String fileMasks, String fileInvalidExt) {

        ArrayList<String> allFilesNames = new ArrayList<String>();

        // extract FileNames
        String[] inFilePaths = filePaths.split(";");
        String[] inFileMasks = fileMasks.split(";");
        for (String filePath : inFilePaths) {
            filePath = Library.correctPathName(filePath);
            for (String fileMask : inFileMasks) {
                String[] files = new File(filePath).list(new FileRegex(fileMask));
                if (files == null) {
                    continue;
                }
                Arrays.sort(files);
                for (String file : files) {
                    // invalidate on process files
                    if (file.contains(".$"))
                        continue;

                    // invalidate already/before processed files
                    if (file.endsWith(fileInvalidExt))
                        continue;

                    allFilesNames.add(filePath + file);
                }
            }
        }
        return allFilesNames;
    }

    public static Connection dbConnectionCheck(Connection inputConnection, String url, String dbUserName, String dbPassword, String exceptionMessage) throws Exception {
        int maxTryCount = 5;
        for (int tryCnt = 0; tryCnt < maxTryCount; tryCnt++) {
            if (inputConnection.isValid(3)) {
                break;
            } else {
                if (inputConnection != null)
                    inputConnection.close();
                try {
                    inputConnection = DriverManager.getConnection(url, dbUserName, dbPassword);
                } catch (Exception ex) {
                    if (tryCnt < maxTryCount - 1)
                        continue;
                    else {
                        Log.log.addExceptLog(ex, true);
                        throw ex;
                    }
                }
            }
        }
        return inputConnection;
    }
  public static Boolean isDir(Path path) {
    if (path == null || !Files.exists(path)) return false;
    else return Files.isDirectory(path);
  }
}
