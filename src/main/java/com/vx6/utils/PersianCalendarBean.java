package com.vx6.utils;

//import Base.Log;

import com.ghasemkiani.util.DateFields;
import com.ghasemkiani.util.SimplePersianCalendar;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: j_farzaneh
 * Date: Apr 22, 2009
 * Time: 7:51:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class PersianCalendarBean {

    public static String getPersianDate(Date julianDate) {
        PersianCalendarEntity spCal = new PersianCalendarEntity();
        String[] dateSplited;
        if (julianDate != null && julianDate.getHours() < 5) {
            Date date = new Date(julianDate.getTime());
            date.setHours(5);
            dateSplited = spCal.getDateFields(date).toString().split("/");
        } else
            dateSplited = spCal.getDateFields(julianDate).toString().split("/");

        if (dateSplited[1].length() == 1)
            dateSplited[1] = '0' + dateSplited[1];
        if (dateSplited[2].length() == 1)
            dateSplited[2] = '0' + dateSplited[2];
        String hours, minute, second;
        try {
            if (julianDate.getHours() < 10)
                hours = '0' + String.valueOf(julianDate.getHours());
            else
                hours = String.valueOf(julianDate.getHours());

            if (julianDate.getMinutes() < 10)
                minute = '0' + String.valueOf(julianDate.getMinutes());
            else
                minute = String.valueOf(julianDate.getMinutes());

            if (julianDate.getSeconds() < 10)
                second = '0' + String.valueOf(julianDate.getSeconds());
            else
                second = String.valueOf(julianDate.getSeconds());
            return dateSplited[0] + "/" + dateSplited[1] + "/" + dateSplited[2] + " " + hours + ":" + minute + ":" + second;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getFirstDayOfNextMonthPersianDate(Date julianDate) {
        PersianCalendarEntity spCal = new PersianCalendarEntity();
        String[] dateSplited;
        if (julianDate != null && julianDate.getHours() < 5) {
            Date date = new Date(julianDate.getTime());
            date.setHours(5);
            dateSplited = spCal.getDateFields(date).toString().split("/");
        } else
            dateSplited = spCal.getDateFields(julianDate).toString().split("/");
        int month= Integer.parseInt(dateSplited[1]) + 1;
        int year = Integer.parseInt(dateSplited[0]);
        if (month==13){
            month=1;
            year++;
        }
        if (month<10)
            return year + "/0" + month + "/01" ;
        else
            return year + "/" + month + "/01";
    }

    public static String getPersianDateSimple(Date julianDate) {
        //13932527195040
        PersianCalendarEntity spCal = new PersianCalendarEntity();
        String[] dateSplited;
        if (julianDate != null && julianDate.getHours() < 5) {
            Date date = new Date(julianDate.getTime());
            date.setHours(5);
            dateSplited = spCal.getDateFields(date).toString().split("/");
        } else
            dateSplited = spCal.getDateFields(julianDate).toString().split("/");

        if (dateSplited[1].length() == 1)
            dateSplited[1] = '0' + dateSplited[1];
        if (dateSplited[2].length() == 1)
            dateSplited[2] = '0' + dateSplited[2];
        String hours, minute, second;
        try {
            if (julianDate.getHours() < 10)
                hours = '0' + String.valueOf(julianDate.getHours());
            else
                hours = String.valueOf(julianDate.getHours());

            if (julianDate.getMinutes() < 10)
                minute = '0' + String.valueOf(julianDate.getMinutes());
            else
                minute = String.valueOf(julianDate.getMinutes());

            if (julianDate.getSeconds() < 10)
                second = '0' + String.valueOf(julianDate.getSeconds());
            else
                second = String.valueOf(julianDate.getSeconds());
            return dateSplited[0] + dateSplited[1] + dateSplited[2] + hours + minute + second;
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getJulianDate(String persianDateStr, String timeStr) {
        SimplePersianCalendar pCal = new SimplePersianCalendar();
        pCal.setDateFields(Integer.parseInt(persianDateStr.substring(0, 4)), Integer.parseInt(persianDateStr.substring(4, 6)) - 1, Integer.parseInt(persianDateStr.substring(6, 8)));
        Date date = pCal.getTime();
        date.setHours(Integer.parseInt(timeStr.substring(0, 2)));
        date.setMinutes(Integer.parseInt(timeStr.substring(2, 4)));
        date.setSeconds(Integer.parseInt(timeStr.substring(4, 6)));
        return date;
    }

    public static String getPDateStr(int deltaDay) {
        //13930525
        try {
            SimplePersianCalendar pCal = new SimplePersianCalendar();
            pCal.add(SimplePersianCalendar.DAY_OF_MONTH, deltaDay);
            DateFields shDate = pCal.getDateFields();
            return String.format("%04d%02d%02d", shDate.getYear(), shDate.getMonth() + 1, shDate.getDay());
        } catch (Exception e) {
            //Log.log.addExceptLog(e, false);
            return "";
        }
    }

}
