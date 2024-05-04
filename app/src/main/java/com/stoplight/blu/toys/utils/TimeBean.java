package com.stoplight.blu.toys.utils;

import com.blankj.utilcode.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeBean {
    private int   mHour;
    private int     mMin;
    private int    mSecond;

    private String pm;

    public String getPm() {
        return pm;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public int   getTotalMillisecond(){
        return (mHour*3600+mMin*60+mSecond)*1000;
    }


    public int   getTotalSecond(){
        return (mHour*3600+mMin*60+mSecond);
    }


    /**
     * 将选择的时间转换为date 求算出与当前时间的总秒差
     * @return
     */
    public int   getSelectTopTotalSecond24H(){

        try {
            SimpleDateFormat df_12=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
            SimpleDateFormat yyyMMdd=new SimpleDateFormat("yyyy-MM-dd");
            Date date1=     df_12.parse(yyyMMdd.format(new Date())+" "+mHour+":"+mHour+":"+mSecond+" "+pm);

            return Integer.valueOf(((date1.getTime()-new Date().getTime())/1000)+"");
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }


    public void toTime(){
        LogUtils.e(mHour+":"+mMin+":"+mSecond+":"+pm);
    }
    private String[] tims;

    public String[] getTims() {
        return tims;
    }

    public void setTims(String[] tims) {
        this.tims = tims;
    }

    public int getmHour() {
        return mHour;
    }

    public void setmHour(int mHour) {
        this.mHour = mHour;
    }

    public int getmMin() {
        return mMin;
    }

    public void setmMin(int mMin) {
        this.mMin = mMin;
    }

    public int getmSecond() {
        return mSecond;
    }

    public void setmSecond(int mSecond) {
        this.mSecond = mSecond;
    }
}
