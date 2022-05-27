package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author Hx
 * @Date 2022/5/22 15:38
 * @Describe
 */
public class TimeUtils {
    public static String getCurrentTime(){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(d);
        return date;
    }
}
