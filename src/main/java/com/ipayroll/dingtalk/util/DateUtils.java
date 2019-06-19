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

    public static Float calculationAnnualLeave(String confirmJoinTime ,String joinWorkingTime){
        Float days = 0F;
        try {
            Date joinWorking = sdf.parse(joinWorkingTime);
            Date now = sdf.parse(sdf.format(new Date()));
            Date confirmJoin = sdf.parse(confirmJoinTime);
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

            //入职天数，入职天数不满一年则：入职天数 *  基数
            long confirmJoinDay = (now.getTime() - confirmJoin.getTime())/(24*3600*1000);
            if (confirmJoinDay <= 365L){
                float unit = days/365;
                days = Float.valueOf(Math.round(confirmJoinDay * unit));
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
