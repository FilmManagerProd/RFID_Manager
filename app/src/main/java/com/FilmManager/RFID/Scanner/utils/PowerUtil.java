package com.FilmManager.RFID.Scanner.utils;

import android.util.Log;

import java.io.FileWriter;

/** @noinspection ALL*/
public class PowerUtil {

//    private static String s1 = "/proc/gpiocontrol/set_id";
    private static final String s2 = "/proc/gpiocontrol/set_uhf";
    private static final String s3 = "/proc/gpiocontrol/set_bd";

    public static void power(String state) {//上电、下电
        try {

            /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){//Android版本大于10.0
                FileWriter localFileWriterOn1 = new FileWriter(s2);
                localFileWriterOn1.write(state);
                localFileWriterOn1.close();
                Log.e("PowerUtil", "power=" + state + " Path=" + s2);
            } else{//Android版本小于10.0
                FileWriter localFileWriterOn = new FileWriter(s2);
                localFileWriterOn.write(state);
                localFileWriterOn.close();
                Log.e("PowerUtil", "power=" + state + " Path=" + s2);
            }*/
            FileWriter localFileWriterOn1 = new FileWriter(s2);
            localFileWriterOn1.write(state);
            localFileWriterOn1.close();
            Log.e("PowerUtil", "power=" + state + " Path=" + s2);

            FileWriter localFileWriterOn = new FileWriter(s3);
            localFileWriterOn.write(state);
            localFileWriterOn.close();
            Log.e("PowerUtil", "power=" + state + " Path=" + s3);

            /*FileWriter RaidPower = new FileWriter(s1);
            RaidPower.write(state);
            RaidPower.close();
            Log.e("PowerUtil", "power=" + state + " Path=" + s1);*/
            Thread.sleep(1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
