package ui.util;

import ui.chat.code.ChatController;
import ui.chat.code.element.group_bar_chat.TalkListItem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtil {
    //存储对话框列表
    public static Map<Integer, TalkListItem> talkMap = new ConcurrentHashMap<Integer, TalkListItem>(16);
    //存储聊天控制器
    public static ChatController chatController;
}
