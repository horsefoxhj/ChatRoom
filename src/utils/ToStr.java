package utils;

/**
 * @Author Hx
 * @Date 2022/5/15 21:40
 */
public class ToStr {

    static <T extends Object> String entity2Str(T t) {
        StringBuilder builder = new StringBuilder("");
        try {
            //TODO:反射获取类对象，组合成数据包返回
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            builder.append("ToStr:error");
        }

        return builder.toString();
    }
}
