package com.vx6.utils;

//import Base.Log;

import com.ghasemkiani.util.DateFields;
import com.ghasemkiani.util.SimplePersianCalendar;
import com.ghasemkiani.util.icu.PersianCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class PersianCalendarBean {

    public static String getPersianDate(Date julianDate) {
        //e.g. 1393/25/27 19:50:40 & 1400/08/01 17:10:17
        PersianCalendarEntity spCal = new PersianCalendarEntity();
        String[] dateSplited;
        Calendar c = Calendar.getInstance();
        c.setTime(julianDate);
        if (c.get(Calendar.HOUR) < 5) {
            Calendar cc = (Calendar) c.clone();
            cc.set(Calendar.HOUR, 5);
            dateSplited = spCal.getDateFields(cc.getTime()).toString().split("/");
        } else
            dateSplited = spCal.getDateFields(julianDate).toString().split("/");

        if (dateSplited[1].length() == 1)
            dateSplited[1] = '0' + dateSplited[1];
        if (dateSplited[2].length() == 1)
            dateSplited[2] = '0' + dateSplited[2];
        String hours, minute, second;
        try {
            if (c.get(Calendar.HOUR) < 10)
                hours = '0' + String.valueOf(c.get(Calendar.HOUR));
            else
                hours = String.valueOf(c.get(Calendar.HOUR));

            if (c.get(Calendar.MINUTE) < 10)
                minute = '0' + String.valueOf(c.get(Calendar.MINUTE));
            else
                minute = String.valueOf(c.get(Calendar.MINUTE));

            if (c.get(Calendar.SECOND) < 10)
                second = '0' + String.valueOf(c.get(Calendar.SECOND));
            else
                second = String.valueOf(c.get(Calendar.SECOND));
            return dateSplited[0] + "/" + dateSplited[1] + "/" + dateSplited[2] + " " + hours + ":" + minute + ":" + second;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getPersianDateSimple(Date julianDate) {
        //e.g. 13932527195040
        PersianCalendarEntity spCal = new PersianCalendarEntity();
        String[] dateSplited;
        Calendar c = Calendar.getInstance();
        c.setTime(julianDate);
        if (c.get(Calendar.HOUR) < 5) {
            Calendar cc = (Calendar) c.clone();
            cc.set(Calendar.HOUR, 5);
            dateSplited = spCal.getDateFields(cc.getTime()).toString().split("/");
        } else
            dateSplited = spCal.getDateFields(julianDate).toString().split("/");

        if (dateSplited[1].length() == 1)
            dateSplited[1] = '0' + dateSplited[1];
        if (dateSplited[2].length() == 1)
            dateSplited[2] = '0' + dateSplited[2];
        String hours, minute, second;
        try {
            if (c.get(Calendar.HOUR) < 10)
                hours = '0' + String.valueOf(c.get(Calendar.HOUR));
            else
                hours = String.valueOf(c.get(Calendar.HOUR));

            if (c.get(Calendar.MINUTE) < 10)
                minute = '0' + String.valueOf(c.get(Calendar.MINUTE));
            else
                minute = String.valueOf(c.get(Calendar.MINUTE));

            if (c.get(Calendar.SECOND) < 10)
                second = '0' + String.valueOf(c.get(Calendar.SECOND));
            else
                second = String.valueOf(c.get(Calendar.SECOND));
            return dateSplited[0] + dateSplited[1] + dateSplited[2] + hours + minute + second;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getFirstDayOfNextMonthOfPersianDate(Date julianDate) {
        PersianCalendarEntity spCal = new PersianCalendarEntity();
        String[] dateSplit;
        if (julianDate != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(julianDate);
            if (c.get(Calendar.HOUR) < 5) {
                c.set(Calendar.HOUR, 5);
            }
            dateSplit = spCal.getDateFields(c.getTime()).toString().split("/");
        } else
            dateSplit = spCal.getDateFields(julianDate).toString().split("/");
        int month = Integer.parseInt(dateSplit[1]) + 1;
        int year = Integer.parseInt(dateSplit[0]);
        if (month == 13) {
            month = 1;
            year++;
        }
        if (month < 10)
            return year + "/0" + month + "/01";
        else
            return year + "/" + month + "/01";
    }

    public static String getLastDayOfMonthOfPersianDate(Date julianDate) {
        String firstDayOfNextMonthOfPersianDate = getFirstDayOfNextMonthOfPersianDate(julianDate).replaceAll("/", "");
        Calendar c = Calendar.getInstance();
        c.setTime(julianDate);
        SimpleDateFormat dateformat = new SimpleDateFormat("HHmmss");  //it will give you the date in the formate that is given in the image
        Date date = getJulianDate(firstDayOfNextMonthOfPersianDate, dateformat.format(c.getTime()));
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, -2);
        return getPersianDateSimple(c.getTime()).substring(0, 8);
    }

    public static Date getJulianDate(String persianDateStr, String timeStr) {
        SimplePersianCalendar pCal = new SimplePersianCalendar();
        pCal.setDateFields(Integer.parseInt(persianDateStr.substring(0, 4)), Integer.parseInt(persianDateStr.substring(4, 6)) - 1, Integer.parseInt(persianDateStr.substring(6, 8)));
        pCal.set(Calendar.HOUR, Integer.parseInt(timeStr.substring(0, 2)));
        pCal.set(Calendar.MINUTE, Integer.parseInt(timeStr.substring(2, 4)));
        pCal.set(Calendar.SECOND, Integer.parseInt(timeStr.substring(4, 6)));
        return pCal.getTime();
    }

    public static String getPDateStr(int deltaDay) {
        //e.g. 13930525
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
