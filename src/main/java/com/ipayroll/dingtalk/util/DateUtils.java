package com.ipayroll.dingtalk.util;

import com.ipayroll.dingtalk.DingTalkApplication;
import org.springframework.boot.SpringApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/5/23
 */
public class DateUtils {

    private static  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static Float calculationAnnualLeave(String regularTime ,String joinWorkingTime, String nowTime){
        Float days = 0F;
        try {
            Date joinWorking = sdf.parse(joinWorkingTime);
            Date now = sdf.parse(nowTime);
            Date regular = sdf.parse(regularTime);
            long day = (now.getTime()-joinWorking.getTime())/(24*3600*1000);
            if (day <= 365L){
                days = 0F;
            }else if (day > 365L && day <= 10*365L){
                days = 5F;
            }else if (day > 10*365L && day <= 20*356L){
                days = 10F;
            }else {
                days = 15F;
            }

            Calendar calendarThisYearLastDay = Calendar.getInstance();
            calendarThisYearLastDay.setTime(new Date());
            calendarThisYearLastDay.set(Calendar.MONTH, 11);
            calendarThisYearLastDay.set(Calendar.DATE, 30);

            Calendar calendarRegular = Calendar.getInstance();
            calendarRegular.setTime(regular);

            //如果是今年转正，计算转正日期起至年底剩余天数*年假基数，四舍五入
            if (calendarRegular.get(Calendar.YEAR) == calendarThisYearLastDay.get(Calendar.YEAR)){
                long lastDays = (calendarThisYearLastDay.getTimeInMillis() - calendarRegular.getTimeInMillis()) / (24*3600*1000);
                float unit = days/365;
                days = Float.valueOf(Math.round(lastDays * unit));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    public static Date getThisYearFirstDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    public static Date getLastYearFirstDay(){
        Calendar calendar = Calendar.getInstance();
        Date thisYear = getThisYearFirstDay();
        calendar.setTime(thisYear);
        calendar.add(Calendar.YEAR,-1);
        return calendar.getTime();
    }

    public static Date getBeforeYearFirstDay(){
        Calendar calendar = Calendar.getInstance();
        Date thisYear = getThisYearFirstDay();
        calendar.setTime(thisYear);
        calendar.add(Calendar.YEAR,-2);
        return calendar.getTime();
    }

}
