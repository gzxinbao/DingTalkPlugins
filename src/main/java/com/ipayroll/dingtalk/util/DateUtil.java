package com.ipayroll.dingtalk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/5/23
 */
public class DateUtil {

    private static  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static Float calculationAnnualLeave(String beginDate, String endDate){
        Float days = 0F;
        try {
            Date begin = sdf.parse(beginDate);
            Date end = sdf.parse(endDate);
            long day = (end.getTime()-begin.getTime())/(24*60*60*1000);
            if (day <= 365L){
                days = 0F;
            }else if (day > 365L && day <= 10*365L){
                days = 5F;
            }else if (day > 10*365L && day <= 20*356L){
                days = 10F;
            }else {
                days = 15F;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }
}
