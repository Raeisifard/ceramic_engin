package com.vx6.utils;

import com.ghasemkiani.util.DateFields;
import com.ghasemkiani.util.PersianCalendarConstants;
import com.ghasemkiani.util.PersianCalendarHelper;
import com.ghasemkiani.util.PersianCalendarUtils;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: j_farzaneh
 * Date: Apr 22, 2009
 * Time: 7:52:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class PersianCalendarEntity extends GregorianCalendar implements PersianCalendarConstants {

    public static final String persianMonths[] = {
            "\u0641\u0631\u0648\u0631\u062F\u06CC\u0646", "\u0627\u0631\u062F\u06CC\u200C\u0628\u0647\u0634\u062A", "\u062E\u0631\u062F\u0627\u062F", "\u062A\u06CC\u0631", "\u0645\u0631\u062F\u0627\u062F", "\u0634\u0647\u0631\u06CC\u0648\u0631", "\u0645\u0647\u0631", "\u0622\u0628\u0627\u0646", "\u0622\u0630\u0631", "\u062F\u06CC",
            "\u0628\u0647\u0645\u0646", "\u0627\u0633\u0641\u0646\u062F"
    };
    public static final String persianWeekDays[] = {
            "\u0634\u0646\u0628\u0647", "\u06CC\u06A9\u200C\u0634\u0646\u0628\u0647", "\u062F\u0648\u0634\u0646\u0628\u0647", "\u0633\u0647\u200C\u0634\u0646\u0628\u0647", "\u0686\u0647\u0627\u0631\u0634\u0646\u0628\u0647", "\u067E\u0646\u062C\u200C\u0634\u0646\u0628\u0647", "\u062C\u0645\u0639\u0647"
    };
    private static final long JULIAN_EPOCH_MILLIS = 0xffff4037bf19b000L;
    private static final long ONE_DAY_MILLIS = 0x5265c00L;
    private static String copyright = "Copyright \251 2003-2005 Ghasem Kiani <ghasemkiani@yahoo.com>. All Rights Reserved.";

    public PersianCalendarEntity() {
    }

    public static String getPersianMonthName(int i) {
        return persianMonths[i];
    }

    public static String getPersianWeekDayName(int i) {
        switch (i) {
            case 7: // '\007'
                return persianWeekDays[0];

            case 1: // '\001'
                return persianWeekDays[1];

            case 2: // '\002'
                return persianWeekDays[2];

            case 3: // '\003'
                return persianWeekDays[3];

            case 4: // '\004'
                return persianWeekDays[4];

            case 5: // '\005'
                return persianWeekDays[5];

            case 6: // '\006'
                return persianWeekDays[6];
        }
        return "";
    }

    public long getJulianDay(Date date) {
        if (date == null)
            setTime(new Date());
        else
            setTime(date);
        return PersianCalendarUtils.div(getTimeInMillis() - 0xffff4037bf19b000L, 86400000D);
    }

    public void setJulianDay(long l) {
        setTimeInMillis(0xffff4037bf19b000L + l * 0x5265c00L + PersianCalendarUtils.mod(getTimeInMillis() - 0xffff4037bf19b000L, 86400000D));
    }

    public void setDateFields(int i, int j, int k) {
        setDateFields(new DateFields(i, j, k));
    }

    public void setDateFields(DateFields datefields) {
        int i = datefields.getYear();
        int j = datefields.getMonth();
        int k = datefields.getDay();
        setJulianDay(PersianCalendarHelper.pj(i <= 0 ? i + 1 : i, j, k));
    }

    public DateFields getDateFields(Date date) {
        //    >libraryDateTime.gdgdgdg
        long l = getJulianDay(date);
        long l1 = PersianCalendarHelper.jp(l);
        long l2 = PersianCalendarUtils.y(l1);
        int i = PersianCalendarUtils.m(l1);
        int j = PersianCalendarUtils.d(l1);
        return new DateFields((int) (l2 <= 0L ? l2 - 1L : l2), i, j);
    }

    public String getPersianMonthName() {
        return getPersianMonthName(getDateFields(null).getMonth());
    }

    public String getPersianWeekDayName() {
        return getPersianWeekDayName(get(7));
    }
}
