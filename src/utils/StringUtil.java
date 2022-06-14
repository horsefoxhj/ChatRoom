package utils;

import java.util.regex.Pattern;

/**
 * @author: Hx
 * @date: 2022年04月06日 23:29
 */
public class StringUtil {

    //判断字符串是否为数字
    public static boolean isNumeric(String s){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(s).matches();
    }
    //判断是否为名字
    public static boolean isName(String s){
        String regex_1 = "^[\\p{L} .'-]+$";
        String regex_2 = "/^[\\u4E00-\\u9FA5]{2,10}(·[\\u4E00-\\u9FA5]{2,10}){0,2}$/";
        return Pattern.matches(regex_1, s);
    }
}
