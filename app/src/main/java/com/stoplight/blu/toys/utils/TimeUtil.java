package com.stoplight.blu.toys.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {


    public static   String formatTime(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;

        return strHour+":"+strMinute + " : " + strSecond+","+strMilliSecond ;
    }


    /**
     * 不够位数的在前面补0，保留num的长度位数字
     * @param str
     * @return
     */
    public static String autoGenericCode(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
            sb.append("0").append(str);// 左补0
//            sb.append(str).append("0");//右补0
            str = sb.toString();
            strLen = str.length();
        }
        return str;

    }



    public  static   TimeBean getTimeBeanBySecond(Long second){
        String time=   formatTime(second*1000);
        TimeBean timeBean=new TimeBean();

        String[] tims=  time.split(":");

        timeBean.setTims(tims);
        timeBean.setmHour(Integer.valueOf(tims[0].trim()));
        timeBean.setmMin(Integer.valueOf(tims[1].trim()));

        try {
            timeBean.setmSecond(Integer.valueOf(tims[2].split(",")[0].trim()));

        }catch (Exception e){
            e.printStackTrace();
        }
        return timeBean;
    }

    public static void main(String[] args) {

        try {

            Date date=new Date();
            //转换成时间格式12小时制
            SimpleDateFormat df_12=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            Date date1=    df_12.parse("2021-04-10 12:12:12");




            Calendar calendar=	Calendar.getInstance();
            calendar.setTime(date1);
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            int second =calendar.get(Calendar.SECOND);
            int pm= 	calendar.get(Calendar.AM_PM);

            System.out.println(pm);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
