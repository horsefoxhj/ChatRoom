package utils;

import com.google.gson.Gson;
import entity.Message;

/**
 * @Author Hx
 * @Date 2022/5/15 21:40
 */
public class JsonUtils {

    private static final Gson gson = new Gson();

    /**
     * 将消息类实体转换为json格式
     *
     * @param message 消息
     * @return str
     */
    public static String Msg2Str(Message message) {
        return gson.toJson(message);
    }

    /**
     * 将字符串转换为消息对象
     *
     * @param str json格式消息
     * @return Message对象
     */
    public static Message Str2Msg(String str) {
        return gson.fromJson(str, Message.class);
    }
}
