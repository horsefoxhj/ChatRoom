package ui.chat.code;

import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

import java.util.Date;

public interface IChatEvent {

    /**
     * 聊天窗口退出操作
     */
    void doQuit();

    /**
     * 发送消息按钮
     *
     * @param userId   用户Id
     * @param talkId   对话Id(好友ID/群组ID)
     * @param talkType 对话框类型；0好友、1群组
     * @param msg      发送消息内容
     * @param msgType  消息类型；0文字消息、1固定表情
     * @param msgDate  发送消息时间
     */
    void doSendMsg(String userId, String talkId, Integer talkType, String msg, Integer msgType, Date msgDate);

    /**
     * 事件处理；开启与好友发送消息 [点击发送消息时候触发 -> 添加到对话框、选中、展示对话列表]
     *
     * @param userId       用户ID
     * @param userFriendId 好友ID
     */
    void doAddTalkUser(String userId, String userFriendId);

    /**
     * 事件处理；开启与群组发送消息
     *
     * @param userId  用户ID
     * @param groupId 群组ID
     */
    void doAddTalkGroup(String userId, String groupId);

    /**
     * 事件处理；删除指定对话框
     *
     * @param userId 用户ID
     * @param roomId 对话框ID
     */
    void doDelTalkUser(String userId, int roomId);

    /**
     * 事件处理；查询用户时，添加到查询列表
     *
     * @param userId   用户ID
     * @param listView 用户列表[非必需使用，同步接口可使用]
     */
    void doLoadNewFriend(String userId, ListView<Pane> listView);

    /**
     * 事件处理；好友搜索[搜索后结果调用添加：addLuckFriend]
     *
     * @param userId 用户ID
     * @param text   搜索关键字
     */
    void doSearchFriend(String userId, String text);

    /**
     * 添加好友事件
     *
     * @param userId   个人ID
     * @param friendId 好友ID
     */
    void doAddUser(String userId, String friendId);

}
