package test;

//import com.ghasemkiani.util.icu.PersianCalendar;

import com.vx6.utils.PersianCalendarBean;

import java.util.Date;

public class TestPersianCalederMethod {
    public static void main(String[] args) {
        System.out.println(PersianCalendarBean.getJulianDate("14000901", "183030"));
        System.out.println(PersianCalendarBean.getPersianDate(new Date()));
        System.out.println(PersianCalendarBean.getFirstDayOfNextMonthOfPersianDate(new Date()));
        System.out.println(PersianCalendarBean.getLastDayOfMonthOfPersianDate(new Date()));
        System.out.println(PersianCalendarBean.getPersianDateSimple(new Date()));
        System.out.println(PersianCalendarBean.getPDateStr(1));
        System.out.println(PersianCalendarBean.getPDateStr(-31));
        System.out.println(PersianCalendarBean.
                getJulianDate("1400/08/01 17:10:17".substring(0, 10).trim().replaceAll("/", "")
                        , "1400/08/01 17:10:17".substring(10).trim().replaceAll(":", "")));
    }
}
