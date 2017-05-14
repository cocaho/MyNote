package com.example.hau.mynote;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hau on 2017/5/7.
 */
public class DateUtil {

    public static String formatDateTime() {
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

}