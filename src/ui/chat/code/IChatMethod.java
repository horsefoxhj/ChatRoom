package ui.chat.code;

import entity.Message;
import entity.RoomInfo;

public interface IChatMethod {


    /**
     * 打开窗口
     */
    void doShow();

    /**
     *
     * @param position 对话框位置；首位0、默认-1
     * @param roomInfo 聊天室信息
     * @param selected 选中[true/false]
     */
    void addTalkBox(int position, RoomInfo roomInfo, Boolean selected);

    /**
     * 填充对话框消息-好友[别人的消息]
     *
     * @param message 消息
     * @param idxFirst 是否设置首位
     * @param selected 是否选中
     * @param isRemind 是否提醒
     */
    void addTalkMsgLeft(Message message, Boolean idxFirst, Boolean selected, Boolean isRemind);

//    /**
//     * 填充对话框消息-群组[别人的消息]
//     *
//     * @param talkId       对话框ID[群组ID]
//     * @param userId       用户ID[群员]
//     * @param userNickName 用户昵称
//     * @param userHead     用户头像
//     * @param msg          消息
//     * @param msgType      消息类型；0文字消息、1固定表情
//     * @param msgDate      时间
//     * @param idxFirst     是否设置首位
//     * @param selected     是否选中
//     * @param isRemind     是否提醒
//     */
//    void addTalkMsgGroupLeft(String talkId, String userId, String userNickName, String userHead, String msg, Integer msgType, Date msgDate, Boolean idxFirst, Boolean selected, Boolean isRemind);


    /**
     * 填充对话框消息[自己的消息]
     * @param message 消息
     * @param idxFirst 是否设置首位
     * @param selected 是否选中
     * @param isRemind 是否提醒
     */
    void addTalkMsgRight(Message message, Boolean idxFirst, Boolean selected, Boolean isRemind);

//    /**
//     * 好友列表添加‘群组’
//     *
//     * @param groupId   群组ID
//     * @param groupName 群组名称
//     * @param groupHead 群组头像
//     */
//    void addFriendGroup(String groupId, String groupName, String groupHead);

    /**
     * 好友列表添加‘用户’
     *
     * @param selected     选中;true/false
     * @param friendId       好友ID
     * @param friendName 好友昵称
     * @param header     好友头像
     */
    void addFriendUser(boolean selected, int friendId, String friendName, String header);

    /**
     * 添加好友
     *
     * @param userId       好友ID
     * @param userNickName 好友昵称
     * @param userHead     好友头像
     * @param status       状态；0添加、1允许、2已添加
     */
    void addNewFriend(int userId, String userNickName, String userHead, Integer status);

//    /**
//     * 工具栏表情框体，位置：X
//     */
//    double getToolFaceX();
//
//    /**
//     * 工具栏表情框体，位置：Y
//     */
//    double getToolFaceY();

}
