package com.github.diorgejorge.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Diorge Jorge on 19/03/2018.
 */
public class DateUtils {
    public static String toString(Date date) {
        return new SimpleDateFormat("ddMMyyyy").format(date);
    }

    public static Date toDate(String date) throws ParseException {
        return new SimpleDateFormat("ddMMyyyy").parse(date);
    }
}
