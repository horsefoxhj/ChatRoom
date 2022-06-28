package ui.chat.code;

import entity.Message;
import entity.RoomInfo;
import entity.User;

public interface IChatMethod {


    /**
     * 打开窗口
     */
    void doShow();

    /**
     * @param position 对话框位置；首位0、默认-1
     * @param roomInfo 聊天室信息
     * @param selected 选中[true/false]
     */
    void addTalkBox(int position, RoomInfo roomInfo, Boolean selected);

    /**
     * 填充对话框消息-好友[别人的消息]
     *
     * @param message  消息
     * @param idxFirst 是否设置首位
     * @param selected 是否选中
     * @param isRemind 是否提醒
     */
    void addTalkMsgLeft(Message message, Boolean idxFirst, Boolean selected, Boolean isRemind);

    /**
     * 填充对话框消息[自己的消息]
     *
     * @param message  消息
     * @param idxFirst 是否设置首位
     * @param selected 是否选中
     * @param isRemind 是否提醒
     */
    void addTalkMsgRight(Message message, Boolean idxFirst, Boolean selected, Boolean isRemind);

    /**
     * 好友列表添加‘群组’
     *
     * @param selected 选中;true/false
     * @param roomInfo 聊天室信息
     */
    void addFriendGroup(boolean selected, RoomInfo roomInfo);

    /**
     * 好友列表添加‘用户’
     *
     * @param selected 选中;true/false
     * @param user     用户信息
     */
    void addFriendUser(boolean selected, User user);

}
